package com.example.graduate.annotation;

import com.example.graduate.codeEnum.LogOperationType;

import java.lang.annotation.*;
/**
 * @author 倪鑫彦
 * @description 自定义注解，放在controller方法上，用于切入指定方法，记录操作日志
 * @since 15:16 2020/11/12
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogAnnotation {
    String description();
    LogOperationType type();
}
