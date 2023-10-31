package com.atguigu.gulimall.order.annotation;

import java.lang.annotation.*;

/*
 * 防重复提交注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenVerify {
    String value() default "";
}
