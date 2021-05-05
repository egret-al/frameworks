package com.spring.annotation;

import java.lang.annotation.*;

/**
 * @author rkc
 * @date 2021/3/15 12:43
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {

    String value() default "";

    String name() default "";
}
