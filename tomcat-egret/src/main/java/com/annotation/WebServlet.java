package com.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebServlet {

    String name() default "";

    String value() default "";

    String description() default "";

    int loadOnStartup() default -1;
}
