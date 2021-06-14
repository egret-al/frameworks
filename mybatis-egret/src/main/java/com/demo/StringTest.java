package com.demo;

import com.demo.entity.User;
import com.ibatis.mapping.SqlCommandType;
import com.ibatis.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author：rkc
 * @date：Created in 2021/6/7 20:24
 * @description：
 */
public class StringTest {

    public static void main(String[] args) throws Exception {
        SqlCommandType sqlCommandType = SqlCommandType.valueOf("select".toUpperCase(Locale.ROOT));
        System.out.println(sqlCommandType == SqlCommandType.SELECT);
    }

    public static boolean isBaseType(Object object) {
        return object.getClass().isPrimitive()
                || object instanceof String
                || object instanceof Integer
                || object instanceof Double
                || object instanceof Float
                || object instanceof Long
                || object instanceof Boolean
                || object instanceof Byte
                || object instanceof Short;
    }

    private static void extracted() throws NoSuchFieldException, IllegalAccessException {
        String sql = "insert into t_user(username, realName, sex, mobile, email, note)\n" +
                "        values(#{username}, #{realName}, #{sex}, #{mobile}, #{email}, #{note});";
        User user = new User();
        user.setUsername("虞姬");
        user.setRealName("虞姬");
        user.setSex("女");
        user.setMobile("1371234121");
        user.setEmail("yj@qq.com");
        user.setNote("noteyj");

        Object[] params = new Object[ObjectUtils.count(sql, '#')];
        int index = 0;
        while (sql.contains("#{")) {
            int startIndex = sql.indexOf("#{");
            int endIndex = sql.indexOf("}");
            String key = sql.substring(startIndex + 2, endIndex);
            System.out.println(key);

            Field field = user.getClass().getDeclaredField(key);
            field.setAccessible(true);
            Object value = field.get(user);
            params[index++] = value;
            sql = sql.replace("#{" + key + "}", "?");
        }
        System.out.println(sql);
        System.out.println(Arrays.toString(params));
    }
}
