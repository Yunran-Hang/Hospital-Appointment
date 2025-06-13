package com.nbdeyy.mapper;

import com.nbdeyy.vo.DepartmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    /**
     * 查询科室信息列表
     * @return
     */
    @Select("select id,category_id,name,sort from department where status = #{status}")
    List<DepartmentVO> getDepartmentList(Integer status);
}
