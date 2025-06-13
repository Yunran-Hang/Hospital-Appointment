package com.nbdeyy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorListPageDTO {
    private String doctorName;
    private Long departmentId;
    private String title;
    private Integer page;
    private Integer pageSize;
}
