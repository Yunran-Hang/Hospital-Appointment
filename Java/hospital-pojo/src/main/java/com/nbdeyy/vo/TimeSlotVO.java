package com.nbdeyy.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeSlotVO implements Serializable {
    private Long id;
    private Long scheduleId;
    private Integer numberId;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer status;
}
