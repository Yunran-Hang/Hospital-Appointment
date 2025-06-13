package com.nbdeyy.service;

import com.nbdeyy.entity.RSAKeyPair;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface RSAKeyService {
    /**
     * 清理过期密钥
     */
    void cleanExpiredKeys();

    /**
     * 获取当前激活的私钥
     */
    PrivateKey getCurrentPrivateKey();

    /**
     * 获取当前激活的公钥
     */
    PublicKey getCurrentPublicKey();

    /**
     * 获取私钥对象
     */
    PrivateKey getPrivateKey(String keyId);

    /**
     * 获取公钥对象
     */
    PublicKey getPublicKey(String keyId);

    /**
     * 轮换密钥
     */
    RSAKeyPair rotateKey();

    /**
     * 生成并保存新的密钥对
     */
    RSAKeyPair generateAndSaveKeyPair();

    /**
     * 获取当前激活的密钥对
     */
    RSAKeyPair getActiveKeyPair();

    /**
     * 是否需要轮换密钥
     * @return
     */
    boolean shouldRotateKey();
}
