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
public class AppointmentInfoDTO {
    // 医生Id
    private Long doctorId;
    // 预约时间段
    private Integer period;
    // 预约日期
    private LocalDate workDate;
}
