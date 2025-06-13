package com.nbdeyy.aspect;

import com.nbdeyy.context.BaseContext;
import com.nbdeyy.entity.OperateLog;
import com.nbdeyy.mapper.OperateLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class OperateLogAspect {

    @Autowired
    private OperateLogMapper operateLogMapper;

    // 定义切入点：拦截 com.nbdeyy.controller 包下的所有方法
    @Around("@annotation(com.nbdeyy.annotation.Log)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 获取方法相关信息
        String className = joinPoint.getTarget().getClass().getName(); // 目标类的全类名
        String methodName = joinPoint.getSignature().getName() ; // 方法名
        Object[] args = joinPoint.getArgs(); // 方法参数

        // 执行目标方法并捕获返回值
        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            // 记录结束时间并计算耗时
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;

            // 构造操作日志对象
            OperateLog operateLog = new OperateLog();
            operateLog.setOperateId(BaseContext.getCurrentId()); // 操作人ID
            operateLog.setOperateName(BaseContext.getUsername());
            operateLog.setOperateTime(LocalDateTime.now());
            operateLog.setClassName(className);
            operateLog.setMethodName(methodName);
            operateLog.setMethodParams(Arrays.toString(args)); // 参数转为字符串
            operateLog.setReturnValue(result != null ? result.toString() : "void"); // 返回值转为字符串
            operateLog.setCostTime(costTime);

            // 保存日志到数据库
            log.info("记录操作日志: {}",operateLog);
            operateLogMapper.insert(operateLog);
        }

        return result;
    }
}