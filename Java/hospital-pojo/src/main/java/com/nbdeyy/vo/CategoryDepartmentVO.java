package com.nbdeyy.vo;

import com.nbdeyy.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDepartmentVO implements Serializable {
    private List<Category> categories;
    private List<DepartmentVO> departments;
}
