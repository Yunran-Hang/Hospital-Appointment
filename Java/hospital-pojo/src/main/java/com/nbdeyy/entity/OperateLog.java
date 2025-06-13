package com.nbdeyy.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperateLog {
    private Long id; //ID
    private Long operateId; //操作人ID
    private LocalDateTime operateTime; //操作时间
    private String className; //操作类名
    private String methodName; //操作方法名
    private String methodParams; //操作方法参数
    private String returnValue; //操作方法返回值
    private Long costTime; //操作耗时

    private String operateName; //操作人名字
}
