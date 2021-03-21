package com.example.graduate.annotation;

import com.example.graduate.codeEnum.LogOperationType;

import java.lang.annotation.*;

/**
 * @author 倪鑫彦
 * @description 自定义切入注解
 * @since 10:18 2021/3/10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AffairLogAnnotation {
    String description();
    LogOperationType type();
}
