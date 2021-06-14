package com.ibatis.annotations;

import java.lang.annotation.*;

/**
 * @author：rkc
 * @date：Created in 2021/6/7 15:04
 * @description：
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    String value();
}
