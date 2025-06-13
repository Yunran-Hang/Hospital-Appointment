package com.nbdeyy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoVO implements Serializable {
    private String username;
    private String realName;
    private String idNumber;
    private Integer gender;
    private String avatar;
    private LocalDate birthDate;
    private String phone;
    private String medicalCard;
}
