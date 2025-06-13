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
public class MyAppointmentListVO implements Serializable {
    private Long appointmentId;
    private Integer appointmentNo;
    private Integer appointmentStatus;
    private String doctorName;
    private String doctorAvatar;
    private String doctorLocation;
    private String doctorTitle;
    private String departmentName;
    private LocalDate appointmentDate;
    private String startTime;
    private String endTime;
}
