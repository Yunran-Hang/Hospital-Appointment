package com.nbdeyy.service.impl;

import com.nbdeyy.entity.RSAKeyPair;
import com.nbdeyy.mapper.RSAKeyPairMapper;
import com.nbdeyy.properties.RSAKeyRotationProperties;
import com.nbdeyy.service.RSAKeyService;
import com.nbdeyy.utils.RsaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RSAKeyServiceImpl implements RSAKeyService {

    @Autowired
    private RSAKeyRotationProperties rsaKeyRotationProperties;

    @Autowired
    private RSAKeyPairMapper rsaKeyPairMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String REDIS_KEY_PREFIX = "rsa:key:";
    private static final String ACTIVE_KEY_ID = "rsa:active:keyid";
    private static final long CACHE_EXPIRE_HOURS = 24; // Redis缓存24小时

    /**
     * 获取当前激活的密钥对
     */
    public RSAKeyPair getActiveKeyPair() {
        // 1. 先从Redis获取
        String activeKeyId = (String) redisTemplate.opsForValue().get(ACTIVE_KEY_ID);
        if (activeKeyId != null) {
            RSAKeyPair cachedKeyPair = (RSAKeyPair) redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + activeKeyId);
            if (cachedKeyPair != null) {
                log.info("从Redis获取密钥对: {}", activeKeyId);
                return cachedKeyPair;
            }
        }

        // 2. Redis中没有，从数据库获取
        RSAKeyPair keyPair = rsaKeyPairMapper.findActiveKeyPair();
        if (keyPair != null) {
            // 缓存到Redis
            cacheKeyPair(keyPair);
            log.info("从数据库获取密钥对并缓存: {}", keyPair.getKeyId());
            return keyPair;
        }

        // 3. 数据库中也没有，生成新的密钥对
        return generateAndSaveKeyPair();
    }

    /**
     * 判断是否需要轮换密钥
     * @return
     */
    public boolean shouldRotateKey() {
        RSAKeyPair currentKey = getActiveKeyPair();
        if (currentKey == null) {
            return true; // 没有密钥，需要生成
        }

        LocalDateTime rotationTime = currentKey.getCreateTime().plusHours(rsaKeyRotationProperties.getInterval());
        return LocalDateTime.now().isAfter(rotationTime);
    }

    /**
     * 根据keyId获取密钥对
     */
    public RSAKeyPair getKeyPairById(String keyId) {
        // 1. 先从Redis获取
        RSAKeyPair cachedKeyPair = (RSAKeyPair) redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + keyId);
        if (cachedKeyPair != null) {
            return cachedKeyPair;
        }

        // 2. 从数据库获取
        RSAKeyPair keyPair = rsaKeyPairMapper.findByKeyId(keyId);
        if (keyPair != null) {
            // 缓存到Redis
            redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + keyId, keyPair, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }

        return keyPair;
    }

    /**
     * 生成并保存新的密钥对
     */
    @Transactional
    public RSAKeyPair generateAndSaveKeyPair() {
        try {
            // 1. 生成密钥对
            Map<String, String> keyMap = RsaUtil.generateKeyPair();

            // 2. 创建实体
            RSAKeyPair keyPair = new RSAKeyPair();
            keyPair.setKeyId(UUID.randomUUID().toString().replace("-", ""));
            keyPair.setPublicKey(keyMap.get("publicKey"));
            keyPair.setPrivateKey(keyMap.get("privateKey"));
            keyPair.setIsActive(true);
            keyPair.setVersion(1);
            keyPair.setExpireTime(LocalDateTime.now().plusDays(rsaKeyRotationProperties.getExpireDays())); // 30天后过期

            // 3. 将之前的密钥设为非激活状态
            deactivateOldKeys();

            // 4. 保存到数据库
            rsaKeyPairMapper.insert(keyPair);

            // 5. 缓存到Redis
            cacheKeyPair(keyPair);

            log.info("生成新的RSA密钥对: {}", keyPair.getKeyId());
            return keyPair;

        } catch (Exception e) {
            log.error("生成RSA密钥对失败", e);
            throw new RuntimeException("生成RSA密钥对失败", e);
        }
    }

    /**
     * 轮换密钥
     */
    @Transactional
    public RSAKeyPair rotateKey() {
        if (shouldRotateKey()) {
            log.info("密钥需要轮换，开始生成新密钥");
            return generateAndSaveKeyPair();
        } else {
            log.debug("密钥仍在有效期内，无需轮换");
            return getActiveKeyPair();
        }
    }

    /**
     * 获取公钥对象
     */
    public PublicKey getPublicKey(String keyId) {
        try {
            RSAKeyPair keyPair = getKeyPairById(keyId);
            if (keyPair == null) {
                throw new RuntimeException("密钥对不存在: " + keyId);
            }
            return RsaUtil.getPublicKey(keyPair.getPublicKey());
        } catch (Exception e) {
            log.error("获取公钥失败: {}", keyId, e);
            throw new RuntimeException("获取公钥失败", e);
        }
    }

    /**
     * 获取私钥对象
     */
    public PrivateKey getPrivateKey(String keyId) {
        try {
            RSAKeyPair keyPair = getKeyPairById(keyId);
            if (keyPair == null) {
                throw new RuntimeException("密钥对不存在: " + keyId);
            }
            return RsaUtil.getPrivateKey(keyPair.getPrivateKey());
        } catch (Exception e) {
            log.error("获取私钥失败: {}", keyId, e);
            throw new RuntimeException("获取私钥失败", e);
        }
    }

    /**
     * 获取当前激活的公钥
     */
    public PublicKey getCurrentPublicKey() {
        RSAKeyPair keyPair = getActiveKeyPair();
        return getPublicKey(keyPair.getKeyId());
    }

    /**
     * 获取当前激活的私钥
     */
    public PrivateKey getCurrentPrivateKey() {
        RSAKeyPair keyPair = getActiveKeyPair();
        return getPrivateKey(keyPair.getKeyId());
    }

    /**
     * 缓存密钥对到Redis
     */
    private void cacheKeyPair(RSAKeyPair keyPair) {
        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + keyPair.getKeyId(), keyPair, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(ACTIVE_KEY_ID, keyPair.getKeyId(), CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    /**
     * 将旧密钥设为非激活状态
     */
    private void deactivateOldKeys() {
        // 将所有is_active=1的记录设为is_active=0
        rsaKeyPairMapper.updateAllIsActiveToFalse();
    }

    /**
     * 清理过期密钥
     */
    public void cleanExpiredKeys() {
        // 删除过期的密钥
        LocalDateTime now = LocalDateTime.now();
        // 这里可以实现删除过期密钥的逻辑
        log.info("清理过期密钥: {}", now);
        rsaKeyPairMapper.deleteAllExpiredKeys(now);
    }
}
