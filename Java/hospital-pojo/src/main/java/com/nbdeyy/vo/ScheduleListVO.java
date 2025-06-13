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
public class ScheduleListVO implements Serializable {
    private Long scheduleId;
    private Integer period;
    private Long doctorId;
    private String doctorName;
    private Long departmentId;
    private String departmentName;
    private LocalDate workDate;
    private Integer maxAppointments;
    private Integer leftAppointments;

    // 每个班次时间间隔
    private Integer timePeriodInterval;
    // 整个排班的开始时间
    private LocalTime startTime;
    // 整个排班结束时间
    private LocalTime endTime;
}
