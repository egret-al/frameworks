package com.spring.resolver;

import com.spring.annotation.Controller;

import java.lang.annotation.Annotation;

/**
 * @author rkc
 * @date 2021/3/16 9:42
 */
public class ControllerAnnotationParser implements AnnotationParser {

    @Override
    public String getBeanName(Annotation annotation) {
        if (annotation instanceof Controller) return ((Controller) annotation).value();
        throw new RuntimeException("@Controller注解解析错误！");
    }
}
