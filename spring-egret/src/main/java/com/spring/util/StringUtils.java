package com.spring.util;

/**
 * @author rkc
 * @date 2021/3/14 14:16
 */
public class StringUtils {

    /**
     * 首字母转小写
     * @param s 待转字符串
     * @return 首字母小写字符串
     */
    public static String toLowerCaseFirstOne(String s) {
        return Character.isLowerCase(s.charAt(0)) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * 首字母转大写
     * @param s 待转字符串
     * @return 首字母大写字符串
     */
    public static String toUpperCaseFirstOne(String s) {
        return Character.isUpperCase(s.charAt(0)) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
