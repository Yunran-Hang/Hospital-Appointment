package com.nbdeyy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nbdeyy.rsa.key.rotation")
@Data
public class RSAKeyRotationProperties {
    private Integer interval;
    private Integer expireDays;
}
