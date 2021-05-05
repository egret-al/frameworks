package com.spring.annotation;

import java.lang.annotation.*;

/**
 * @author rkc
 * @date 2021/3/12 13:41
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Component {

    String value() default "";
}
