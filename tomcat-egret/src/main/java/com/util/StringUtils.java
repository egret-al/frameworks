package com.util;

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

    /**
     * uri模糊匹配
     * @param uri 被匹配的uri
     * @param str 进行对比的uri
     * @return 是否匹配
     */
    public static boolean uriMatch(String uri, String str) {
        char[] uriArr = uri.toCharArray();
        char[] strArr = str.toCharArray();
        int i = 0, j = 0;
        while (i < uriArr.length && j < strArr.length) {
            if (uriArr[i] == strArr[j]) {
                //对应字符相等，移动指针
                i++;
                j++;
            } else if (strArr[j] == '*' && j == strArr.length - 1) {
                //如果最后一个字符是通配符，说明后续路径无需匹配
                return true;
            } else if (strArr[j] == '*') {
                //如果当前模糊串含有通配符，当前指针不动，移动i指针，直到遇到/
                while (uriArr[i] != '/') {
                    i++;
                    if (i == uriArr.length - 1) {
                        //i已经指向了uri的最后一个字符，说明已经匹配成功
                        return true;
                    }
                }
                j++;
            } else {
                return false;
            }
        }
        return i == uriArr.length - 1;
    }

    public static boolean uriMatching(String originalUri, String uri) {
        if ("/*".equals(uri)) {
            return true;
        }
        //按 * 切割字符串
        String[] reg_split = uri.split("\\*");
        int index = 0, reg_len = reg_split.length;
        //b代表匹配模式的最后一个字符是否是 '*' ,因为在split方法下最后一个 * 会被舍弃
        boolean b = uri.charAt(uri.length() - 1) == '*';
        while (originalUri.length() > 0) {
            //如果匹配到最后一段,比如这里reg的landingsuper
            if (index == reg_len) {
                if (b)//如果reg最后一位是 * ,代表通配,后面的就不用管了,返回true
                    return true;
                else  //后面没有通配符 * ,但是input长度还没有变成0 (此时input = context=%7B%22nid%22%3...),显然不匹配
                {
                    return false;
                }
            }
            String r = reg_split[index++];
            int indexOf = originalUri.indexOf(r);
            if (indexOf == -1) {
                return false;
            }
            //前面匹配成功的就可以不用管了,截取掉直接从新地方开始
            originalUri = originalUri.substring(indexOf + r.length());
        }
        return true;
    }
}
