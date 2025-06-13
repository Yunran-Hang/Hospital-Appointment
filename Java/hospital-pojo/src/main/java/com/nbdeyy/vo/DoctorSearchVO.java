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
public class DoctorSearchVO implements Serializable {
    private Long departmentId;
    private String departmentName;
    private Long doctorId;
    private String name;
    private String title;
    private String avatar;
    private String campus;
    private Double fee;
    private LocalDate workDate;
    private Integer period;
    private Integer totalSchedule;
    private Integer leftAppointment;
    private Integer maxAppointment;
}
