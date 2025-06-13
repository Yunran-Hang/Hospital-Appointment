package com.nbdeyy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nbdeyy.alicaptcha")
@Data
public class AliCaptchaProperties {

    private String ACCESS_KEY_ID;
    private String ACCESS_KEY_SECRET;

}
