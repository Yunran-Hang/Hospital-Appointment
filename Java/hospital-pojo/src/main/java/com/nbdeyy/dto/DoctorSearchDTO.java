package com.nbdeyy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorSearchDTO {
    // 部门ID
    private Long departmentId;
    // 预约时间段
    private Integer period;
    private LocalDate date;

    private String searchKeyword;

    private Integer page;
    private Integer pageSize;
}
