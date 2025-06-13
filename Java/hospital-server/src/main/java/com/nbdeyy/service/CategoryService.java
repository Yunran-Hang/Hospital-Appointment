package com.nbdeyy.service;

import com.nbdeyy.vo.CategoryDepartmentVO;

public interface CategoryService {
    /**
     * 获取所有分类和部门
     * @return
     */
    CategoryDepartmentVO getCategoryList();
}
