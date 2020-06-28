package com.atguigu.gmall.index.annotation;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {
    String prefix() default "";
    String cacheKey() default "";
    // 分钟，默认七天
    int cacheTime() default 10080;
    // 分钟，默认两天
    int randomTime() default 2880;

    String lockName() default "";
}
