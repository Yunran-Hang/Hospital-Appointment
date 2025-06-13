package com.nbdeyy.mapper;

import com.nbdeyy.entity.Category;
import com.nbdeyy.vo.DepartmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 获得所有一级分类列表
     * @return
     */
    @Select("select * from category")
    List<Category> getCategoryList();

    /**
     * 查询所有部门列表(部分字段)
     * @return
     */
    @Select("select id,category_id,name,sort from department where status = 1 order by sort asc")
    List<DepartmentVO> getDepartmentList();
}
