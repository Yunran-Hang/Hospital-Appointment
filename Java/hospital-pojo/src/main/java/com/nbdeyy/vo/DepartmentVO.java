package com.nbdeyy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentVO implements Serializable {
    private Long id;
    private Long categoryId;
    private String name;
    private Integer sort;
}
