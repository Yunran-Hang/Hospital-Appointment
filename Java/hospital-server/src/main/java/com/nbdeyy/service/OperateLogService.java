package com.nbdeyy.service;

import com.nbdeyy.dto.OperateLogPageDTO;
import com.nbdeyy.result.PageResult;

public interface OperateLogService {
    /**
     * 分页查询操作日志
     * @param operateLogPageDTO
     * @return
     */
    PageResult pageQuery(OperateLogPageDTO operateLogPageDTO);
}
