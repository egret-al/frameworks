package com.ibatis.reflection;

import com.ibatis.annotations.Param;
import com.ibatis.config.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author：rkc
 * @date：Created in 2021/6/8 14:38
 * @description：
 */
public class ParamNameResolver {

    private final SortedMap<Integer, String> names;
    private boolean hasParamAnnotation;
    public static final String GENERIC_NAME_PREFIX = "param";

    public ParamNameResolver(Configuration configuration, Method method) {
        SortedMap<Integer, String> map = new TreeMap<>();
        //得到方法参数类型和参数上的注解
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        //遍历参数，解析上面标注的@Param注解，得到name
        for (int paramIndex = 0; paramIndex < parameterAnnotations.length; paramIndex++) {
            String name = null;
            for (Annotation annotation : parameterAnnotations[paramIndex]) {
                if (annotation instanceof Param) {
                    hasParamAnnotation = true;
                    name = ((Param) annotation).value();
                    break;
                }
            }
            if (name == null) {
                //没有指定@Param注解，使用参数的索引作为它的name
                name = String.valueOf(map.size());
            }
            //将解析后得到的结果放入map中
            map.put(paramIndex, name);
        }
        names = Collections.unmodifiableSortedMap(map);
    }

    public Object getNamedParams(Object[] args) {
        if (args == null || names.size() == 0) {
            return null;
        }
        if (args.length == 1) {
            //如果只有一个参数，则不需要用map进行映射，直接返回
            return args[names.firstKey()];
        }
        Map<String, Object> param = new HashMap<>();
//        int i = 0;
        for (Map.Entry<Integer, String> entry : names.entrySet()) {
            param.put(entry.getValue(), args[entry.getKey()]);
            //加入一个生成的参数名
//            String genericParamName = GENERIC_NAME_PREFIX + (i + 1);
//            if (!names.containsValue(genericParamName)) {
//                param.put(genericParamName, args[entry.getKey()]);
//            }
//            i++;
        }
        return param;
    }
}
