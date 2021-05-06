package com.spring.annotation;

import java.lang.annotation.*;

/**
 * 当注入的属性是接口时，如果不添加次注解会默认以属性的名称进行查找注入，
 * 添加该注解后，则以该注解查找到的为准进行注入
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Qualifier {

    String value() default "";
}
