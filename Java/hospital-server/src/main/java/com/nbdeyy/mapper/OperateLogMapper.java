package com.nbdeyy.mapper;

import com.github.pagehelper.Page;
import com.nbdeyy.dto.OperateLogPageDTO;
import com.nbdeyy.entity.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperateLogMapper {

    /**
     * 插入日志数据
     * @param operateLog
     */
    @Insert("insert into operate_log (operate_id, class_name, method_name, method_params, return_value, cost_time, operate_time) " +
            "VALUES (#{operateId},#{className},#{methodName},#{methodParams},#{returnValue},#{costTime},#{operateTime})")
    public void insert(OperateLog operateLog);

    /**
     * 分页查询操作日志
     * @param operateLogPageDTO
     * @return
     */
    Page<OperateLog> pageQuery(OperateLogPageDTO operateLogPageDTO);
}
