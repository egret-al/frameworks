package com.spring.resolver;

import com.spring.annotation.Service;

import java.lang.annotation.Annotation;

/**
 * @author rkc
 * @date 2021/3/16 9:41
 */
public class ServiceAnnotationParser implements AnnotationParser {

    @Override
    public String getBeanName(Annotation annotation) {
        if (annotation instanceof Service) return ((Service) annotation).value();
        throw new RuntimeException("@Service注解解析错误！");
    }
}
