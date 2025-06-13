package com.nbdeyy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Doctor implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String avatar;
    private Long departmentId;
    private String campus;
    private String title;
    private String specialty;
    private Long totalSchedule;
    private String description;
    private Double fee;
    private String location;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
