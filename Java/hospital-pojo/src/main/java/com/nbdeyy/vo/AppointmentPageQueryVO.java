package com.nbdeyy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentPageQueryVO implements Serializable {
    // 预约Id
    private Long appointmentId;
    // 医生Id
    private Long doctorId;
    // 医生姓名
    private String doctorName;
    // 病人Id
    private String patientId;
    // 病人姓名
    private String patientName;
    // 病人电话
    private String patientPhone;
    // 预约时段
    private Integer period;
    // 状态
    private Integer status;
    // 预约日期
    private LocalDate appointmentDate;
    // 预约号
    private Integer appointmentNo;
    // 性别
    private Integer gender;
    // 年龄
    private Integer age;
    // 科室名称
    private String departmentName;

    // 预约时间
    private LocalTime startTime;
    // 院区
    private String campus;
    // 职称
    private String title;
    // 诊金
    private String fee;
    // 位置
    private String location;
}
