package com.nbdeyy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDetailVO implements Serializable {
    private Long id;
    private String name;
    private String avatar;
    private Long departmentId;
    private String departmentName;
    private String campus;
    private String title;
    private String specialty;
    private Long totalSchedule;
    private String description;
    private Double fee;
    private String location;
    private Integer status;

    private Integer period;
}