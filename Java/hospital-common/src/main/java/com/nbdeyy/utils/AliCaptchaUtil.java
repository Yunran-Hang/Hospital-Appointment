package com.nbdeyy.utils;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.captcha20230305.AsyncClient;
import com.aliyun.sdk.service.captcha20230305.models.*;
import com.nbdeyy.constant.MessageConstant;
import com.nbdeyy.exception.CaptchaVerifyFailException;
import com.nbdeyy.properties.AliCaptchaProperties;
import com.nbdeyy.result.Result;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class AliCaptchaUtil {
    @Autowired
    private AliCaptchaProperties aliCaptchaProperties;
    public void verifyIntelligentCaptcha(String CaptchaVerifyParam) throws ExecutionException, InterruptedException {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
                .accessKeyId(System.getenv(aliCaptchaProperties.getACCESS_KEY_ID()))
                .accessKeySecret(System.getenv(aliCaptchaProperties.getACCESS_KEY_SECRET()))
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build());
        AsyncClient client = AsyncClient.builder()
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                // Endpoint 请参考 https://api.aliyun.com/product/captcha
                                .setEndpointOverride("captcha.cn-shanghai.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();
        VerifyIntelligentCaptchaRequest verifyIntelligentCaptchaRequest = VerifyIntelligentCaptchaRequest.builder()
                .captchaVerifyParam(CaptchaVerifyParam)
                .sceneId("u85r32wr")
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();
        CompletableFuture<VerifyIntelligentCaptchaResponse> response = client.verifyIntelligentCaptcha(verifyIntelligentCaptchaRequest);
        VerifyIntelligentCaptchaResponse resp = response.get();

        client.close();
        Boolean success = resp.getBody().getSuccess();
        Boolean verifyResult = resp.getBody().getResult().getVerifyResult();
        if (!success || !verifyResult) throw new CaptchaVerifyFailException(MessageConstant.CAPTCHA_VERIFY_FAIL);
    }
}
