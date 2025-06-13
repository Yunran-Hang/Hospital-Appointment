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
public class AppointmentListPageDTO {
    // 医生姓名
    private String doctorName;
    // 患者姓名
    private String patientName;
    // 预约日期
    private LocalDate appointmentDate;
    // 预约状态
    private Integer status;
    // 页数
    private Integer page;
    // 每页数量
    private Integer pageSize;
}
