package com.servlet;

import com.alibaba.fastjson.JSON;
import com.annotation.RequestMapping;
import com.annotation.RequestMethod;
import com.annotation.WebServlet;
import com.http.HttpServlet;
import com.http.HttpServletRequest;
import com.http.HttpServletResponse;
import com.spring.annotation.Controller;
import com.spring.config.BeanDefinition;
import com.spring.support.AbstractApplicationContext;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author：rkc
 * @date：Created in 2021/5/8 18:49
 * @description：
 */
@WebServlet(value = "/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    /* 映射map，一个uri对应一个Method */
    private final static Map<String, Method> handlerMapping = new ConcurrentHashMap<>(256);
    /* 映射控制器，一个uri对应一个Controller */
    private final static Map<String, Object> controllerMapping = new ConcurrentHashMap<>(256);

    static {
        initHandlerMapping();
    }

    /**
     * 请求的处理，转发。主要是根据uri来进行选择为该请求服务的method
     * @param request 当前请求
     * @param response 当前响应
     * @throws IOException IOException
     */
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //返回结果统一为json字符串
        response.setContentType("application/json;charset=UTF-8");
        if (handlerMapping.isEmpty()) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("\r\n没有对应的处理请求！".getBytes(StandardCharsets.UTF_8));
            return;
        }
        String url = request.getRequestURI();
        //自己的http服务器不存在contextPath
//        String contextPath = request.getContextPath();
//        url = url.replace(contextPath, "").replaceAll("/+", "/");
        Method handlerMethod = handlerMapping.get(url);
        if (handlerMethod == null) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("\r\n没有找到该页面！".getBytes(StandardCharsets.UTF_8));
            return;
        }

        String requestType = request.getMethod();
        HandlerAdapter handlerAdapter = new SimpleHandlerAdapter();
        Object[] parameterValues = handlerAdapter.handle(request, response, handlerMethod);

        //仅支持GET和POST请求！
        if ("GET".equals(requestType) && checkRequestMethod(handlerMethod, requestType)) {
            //GET请求调用方法
            Object result = invokeMethod(controllerMapping.get(url), handlerMethod, parameterValues);
            if (!Objects.isNull(result)) {
                response.begin();
                response.getWriter().write(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
            }
            return;
        } else if ("POST".equals(requestType) && checkRequestMethod(handlerMethod, requestType)){
            //POST调用方法
            Object result = invokeMethod(controllerMapping.get(url), handlerMethod, parameterValues);
            if (!Objects.isNull(result)) {
                response.begin();
                response.getWriter().write(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
            }
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("\r\n不支持该类型！".getBytes(StandardCharsets.UTF_8));
        throw new RuntimeException("不支持的请求类型！" + requestType);
    }

    /**
     * 业务逻辑的调用，也就是controller对应的method的代码
     * @param obj 调用对象
     * @param method 回调方法
     * @param parameters 参数列表
     * @return 返回结果，默认以json的形式进行回写
     */
    private Object invokeMethod(Object obj, Method method, Object[] parameters) {
        try {
            return method.invoke(obj, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查该方法是否能够接收对应类型的请求
     * @param method 当前处理方法
     * @param requestType 请求类型
     * @return 是否能够处理
     */
    private boolean checkRequestMethod(Method method, String requestType) {
        RequestMethod[] requestMethods = method.getAnnotation(RequestMapping.class).method();
        for (RequestMethod rm : requestMethods) {
            if (rm.name().equals(requestType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doDispatch(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doDispatch(req, resp);
    }

    /**
     * IoC扫描后，将里面的所有bean进行过滤，只只要得到controller的bean并将其放入map中进行管理
     */
    private static void initHandlerMapping() {
        AbstractApplicationContext applicationContext = SpringApplication.getApplicationContext();
        //得到所有的Spring扫描的BeanDefinition
        Map<String, BeanDefinition> beanDefinitions = applicationContext.getDefaultListableBeanFactory().getBeanDefinitions();
        //找出标注了@Controller注解的bean
        for (String beanName : beanDefinitions.keySet()) {
            Class<?> beanClass = applicationContext.getBeanDefinition(beanName).getBeanClass();
            if (beanClass.isAnnotationPresent(Controller.class)) {
                //该bean是控制器，需要交由SpringMVC进行处理
                String baseUrl = "";
                if (beanClass.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = beanClass.getAnnotation(RequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                //处理所有的方法
                Method[] methods = beanClass.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(RequestMapping.class)) {
                        continue;
                    }
                    //取出方法上的@RequestMapping注解
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    String url = requestMapping.value();
                    url = (baseUrl + "/" + url).replaceAll("/+", "/");
                    //将对应处理的方法和控制器放入map中
                    handlerMapping.put(url, method);
                    controllerMapping.put(url, applicationContext.getBean(beanName, beanClass));
                    System.out.println(beanClass.getSimpleName() + "." + method.getName() + "->" + url);
                }
            }
        }
    }
}
