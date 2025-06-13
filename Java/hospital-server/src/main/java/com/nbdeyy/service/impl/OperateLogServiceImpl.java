package com.nbdeyy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nbdeyy.dto.OperateLogPageDTO;
import com.nbdeyy.entity.OperateLog;
import com.nbdeyy.mapper.OperateLogMapper;
import com.nbdeyy.result.PageResult;
import com.nbdeyy.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    private OperateLogMapper operateLogMapper;

    /**
     * 分页查询操作日志
     * @param operateLogPageDTO
     * @return
     */
    public PageResult pageQuery(OperateLogPageDTO operateLogPageDTO) {
        // 开启分页查询
        PageHelper.startPage(operateLogPageDTO.getPage(), operateLogPageDTO.getPageSize());
        Page<OperateLog> p = operateLogMapper.pageQuery(operateLogPageDTO);
        return new PageResult(p.getTotal(), p.getResult());
    }
}
