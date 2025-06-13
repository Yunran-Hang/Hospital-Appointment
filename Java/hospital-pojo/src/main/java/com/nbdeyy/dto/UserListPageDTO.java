package com.nbdeyy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserListPageDTO {

    private Integer page;
    private Integer pageSize;

    private String username;
    private String realName;
    private String avatar;
    private String phone;
    private LocalDate birthDate;
    private Integer gender;
    private String idNumber;
    private String medicalCard;
    private Integer status;
}
