package com.spring.beans;

/**
 * @author：rkc
 * @date：Created in 2021/5/7 19:08
 * @description：
 */
public class BeansException extends RuntimeException {

    public BeansException(String msg) {
        super(msg);
    }

    public BeansException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
