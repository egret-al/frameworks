package com.spring.resolver;

import com.spring.annotation.Repository;

import java.lang.annotation.Annotation;

/**
 * @author rkc
 * @date 2021/3/16 9:41
 */
public class RepositoryAnnotationParser implements AnnotationParser {

    @Override
    public String getBeanName(Annotation annotation) {
        if (annotation instanceof Repository) {
            return ((Repository) annotation).value();
        }
        throw new RuntimeException("@Repository注解解析错误！");
    }
}
