package com.nbdeyy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String realName;
    private String idNumber;
    private Integer gender;
    private String avatar;
    private LocalDate birthDate;
    private String phone;
    private String medicalCard;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
