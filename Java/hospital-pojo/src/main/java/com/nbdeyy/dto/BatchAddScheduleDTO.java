package com.nbdeyy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchAddScheduleDTO {
    private List<Long> doctorIds;
    private List<Integer> workDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer timePeriodInterval;
    private Integer maxAppointments;
    private Integer period;
    private LocalTime startTime;
    private LocalTime endTime;
}
