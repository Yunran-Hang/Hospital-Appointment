package com.nbdeyy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {
    private String username;
    private String realName;
    private String idCard;
    private String medicalCard;
    private String password;
    private Integer gender;
    private LocalDate birthDate;
}
