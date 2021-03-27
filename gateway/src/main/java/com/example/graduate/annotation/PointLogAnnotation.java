package com.example.graduate.annotation;

import java.lang.annotation.*;
/**
 * @author 倪鑫彦
 * @description 切入积分相关操作，记录日志
 * @since 14:58 2021/3/21
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PointLogAnnotation {
    String note();
}
