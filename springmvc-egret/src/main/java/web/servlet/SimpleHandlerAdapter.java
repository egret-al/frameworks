package web.servlet;

import com.alibaba.fastjson.JSON;
import web.annotation.RequestBody;
import web.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;

/**
 * @author：rkc
 * @date：Created in 2021/5/10 8:54
 * @description：
 */
public class SimpleHandlerAdapter implements HandlerAdapter {

    @Override
    public Object[] handle(HttpServletRequest request, HttpServletResponse response, Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] parameterValues = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String typeName = parameter.getType().getSimpleName();
            if ("HttpServletRequest".equals(typeName)) {
                parameterValues[i] = request;
                continue;
            }
            if ("HttpServletResponse".equals(typeName)) {
                parameterValues[i] = response;
                continue;
            }
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                parameterValues[i] = request.getParameter(requestParam.value());
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                //得到请求体，将请求体转换为Java对象并进行注入
                try {
                    parameterValues[i] = getJavaObject(request.getInputStream(), parameter.getType());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new RuntimeException("存在参数没有标注RequestParam或者RequestBody注解");
            }
        }
        return parameterValues;
    }

    private Object getJavaObject(InputStream inputStream, Class<?> clazz) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String str = null;
        while ((str = reader.readLine()) != null) {
            stringBuilder.append(str);
        }
        return JSON.parseObject(stringBuilder.toString(), clazz);
    }
}
