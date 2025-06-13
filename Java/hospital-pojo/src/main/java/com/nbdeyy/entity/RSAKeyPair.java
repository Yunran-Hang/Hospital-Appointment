package com.nbdeyy.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RSAKeyPair implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String keyId;
    private String publicKey;
    private String privateKey;
    private Boolean isActive;
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime expireTime;
}