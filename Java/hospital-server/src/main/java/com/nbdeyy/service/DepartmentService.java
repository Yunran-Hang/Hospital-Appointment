package com.nbdeyy.service;

import com.nbdeyy.vo.DepartmentVO;

import java.util.List;

public interface DepartmentService {
    /**
     * 查询所有科室列表
     * @return
     */
    List<DepartmentVO> getDepartmentList();
}
