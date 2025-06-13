package com.nbdeyy.service.impl;

import com.nbdeyy.entity.Category;
import com.nbdeyy.mapper.CategoryMapper;
import com.nbdeyy.service.CategoryService;
import com.nbdeyy.vo.CategoryDepartmentVO;
import com.nbdeyy.vo.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获得所有一二级分类
     * @return
     */
    public CategoryDepartmentVO getCategoryList() {
        List<Category> categories = categoryMapper.getCategoryList();
        List<DepartmentVO> departments = categoryMapper.getDepartmentList();
        return new CategoryDepartmentVO(categories,departments);
    }
}
