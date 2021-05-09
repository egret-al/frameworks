package web.annotation;

import java.lang.annotation.*;

/**
 * 用于作用在类或者方法上的映射注解，每个标注了该注解的方法都会处理对应的URL请求
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value() default "";

    RequestMethod[] method() default {RequestMethod.GET};
}
