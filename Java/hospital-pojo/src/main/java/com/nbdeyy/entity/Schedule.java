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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;
    // 主键Id
    private Long id;
    // 医生Id
    private Long doctorId;
    // 排班日期
    private LocalDate workDate;
    // 排班时段 1上午 2下午
    private Integer period;
    // 总号源
    private Integer maxAppointments;
    // 剩余号源
    private Integer leftAppointments;
    // 创建时间
    private LocalDateTime createTime;
    // 最后更新时间
    private LocalDateTime updateTime;

    // 每个班次时间间隔
    private Integer timePeriodInterval;
    // 整个排班的开始时间
    private LocalTime startTime;
    // 整个排班结束时间
    private LocalTime endTime;
}
