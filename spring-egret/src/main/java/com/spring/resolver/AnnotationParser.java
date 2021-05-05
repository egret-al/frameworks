package com.spring.resolver;

import java.lang.annotation.Annotation;

/**
 * @author rkc
 * @date 2021/3/16 9:40
 */
public interface AnnotationParser {

    String getBeanName(Annotation annotation);
}
