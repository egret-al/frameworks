package com.spring.resolver;

import com.spring.annotation.Component;

import java.lang.annotation.Annotation;

/**
 * @author rkc
 * @date 2021/3/16 9:41
 */
public class ComponentAnnotationParser implements AnnotationParser {

    @Override
    public String getBeanName(Annotation annotation) {
        if (annotation instanceof Component) return ((Component) annotation).value();
        throw new RuntimeException("@Component注解解析错误！");
    }
}
