package com.nbdeyy.task;

import com.nbdeyy.service.RSAKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// TODO: 未开启每日自动执行秘钥轮换
@Slf4j
@Component
public class KeyRotationTask {

    @Autowired
    private RSAKeyService rsaKeyService;

    /**
     * 每天凌晨2点执行密钥轮换检查
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkKeyRotation() {
        try {
            log.info("开始检查密钥轮换");
            // 这里可以添加轮换条件判断
            // 比如密钥使用时间超过一定天数就轮换

            rsaKeyService.rotateKey();

            // 清理过期密钥
            rsaKeyService.cleanExpiredKeys();
            log.info("密钥轮换检查完成");
        } catch (Exception e) {
            log.error("密钥轮换检查失败", e);
        }
    }
}
