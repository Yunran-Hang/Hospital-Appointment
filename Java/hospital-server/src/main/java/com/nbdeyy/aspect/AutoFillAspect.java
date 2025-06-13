package com.nbdeyy.aspect;

import com.nbdeyy.annotation.AutoFill;
import com.nbdeyy.constant.AutoFillConstant;
import com.nbdeyy.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.nbdeyy.mapper.*.*(..)) && @annotation(com.nbdeyy.annotation.AutoFill )")
    public void autoFillPointCut(){}

    /**
     * 前置通知
     */
    @Before("autoFillPointCut()")
    public void autoFIll(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");

        // 获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); // 获得方法上的注解对象
        OperationType operationType = autoFill.value(); // 获得数据库操作类型，就是注解的值
        // 获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }
        Object entity = args[0];
        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        // 根据当前不同的操作类型，为对应的属性通过 反射 进行赋值
        if (operationType == OperationType.INSERT){
            try {
                // 获得方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                // 通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }else if (operationType == OperationType.UPDATE){

            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                setUpdateTime.invoke(entity,now);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
