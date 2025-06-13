package com.nbdeyy.service.impl;

import com.nbdeyy.constant.StatusConstant;
import com.nbdeyy.mapper.DepartmentMapper;
import com.nbdeyy.service.DepartmentService;
import com.nbdeyy.vo.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 查询所有科室列表
     * @return
     */
    public List<DepartmentVO> getDepartmentList() {
        List<DepartmentVO> list = departmentMapper.getDepartmentList(StatusConstant.ENABLE);
        return list;
    }
}
