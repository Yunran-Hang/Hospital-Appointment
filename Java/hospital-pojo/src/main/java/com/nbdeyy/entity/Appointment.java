package com.nbdeyy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long scheduleId;
    private Long timeSlotId;
    private Integer appointmentNo;
    private LocalDate appointmentDate;
    private Integer period;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer status;
    private Double fee;
    private String cancelReason;
    private LocalDateTime createTime;
     private LocalDateTime updateTime;
}
