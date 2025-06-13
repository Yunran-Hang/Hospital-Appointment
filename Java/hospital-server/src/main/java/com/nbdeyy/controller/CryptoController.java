package com.nbdeyy.controller;

import com.nbdeyy.result.Result;
import com.nbdeyy.service.RSAKeyService;
import com.nbdeyy.utils.RsaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;


@RestController
@Slf4j
@RequestMapping("/crypto")
public class CryptoController {

    @Autowired
    private RSAKeyService rsaKeyService;

    /**
     * 获取服务器公钥
     */
    @GetMapping("/publicKey")
    public Result getPublicKey() {
        log.info("获取公钥信息...");
        PublicKey publicKey = rsaKeyService.getCurrentPublicKey();
        return Result.success(RsaUtil.getStringPublicKey(publicKey));
    }
}
