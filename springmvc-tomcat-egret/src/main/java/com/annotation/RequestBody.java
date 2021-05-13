package com.annotation;

import java.lang.annotation.*;

/**
 * @author：rkc
 * @date：Created in 2021/5/9 20:54
 * @description：
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {

    boolean required() default true;
}
