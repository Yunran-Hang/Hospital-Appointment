package com.nbdeyy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperateLogPageDTO {
    private Integer page;
    private Integer pageSize;

    private String className;
    private String methodName;
    private LocalDateTime operateTimeStart;
    private LocalDateTime operateTimeEnd;
}
