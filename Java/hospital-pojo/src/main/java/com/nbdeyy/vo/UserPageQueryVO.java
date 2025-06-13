package com.nbdeyy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPageQueryVO {

    private Long id;
    private String username;
    private String realName;
    private String avatar;
    private Integer gender;
    private String idNumber;
    private LocalDate birthDate;
    private String phone;
    private String medicalCard;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
