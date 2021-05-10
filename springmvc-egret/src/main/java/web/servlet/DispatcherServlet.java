package web.servlet;

import com.alibaba.fastjson.JSON;
import com.spring.annotation.Controller;
import com.spring.config.BeanDefinition;
import com.spring.context.AnnotationConfigApplicationContext;
import com.spring.support.AbstractApplicationContext;
import web.annotation.RequestMapping;
import web.annotation.RequestMethod;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author：rkc
 * @date：Created in 2021/5/8 18:49
 * @description：
 */
@WebServlet(value = "/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    /* 用于加载配置文件 */
    private final Properties properties = new Properties();
    /* 自己手写的Spring IoC容器 */
    private AbstractApplicationContext applicationContext;
    /* 映射map，一个uri对应一个Method */
    private final Map<String, Method> handlerMapping = new ConcurrentHashMap<>(256);
    /* 映射控制器，一个uri对应一个Controller */
    private final Map<String, Object> controllerMapping = new ConcurrentHashMap<>(256);
    /* 默认配置文件 */
    private final static String CONFIG_LOCATION = "application.properties";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        loadConfig();
        String[] scanPackages = properties.getProperty("spring.scan-package").split(",");
        List<String> packageList = new ArrayList<>(Arrays.asList(scanPackages));
        //将扫描包加载到spring的上下文中
        applicationContext = new AnnotationConfigApplicationContext(packageList);
        initHandlerMapping();
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //返回结果统一为json字符串
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        if (handlerMapping.isEmpty()) {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.getOutputStream().write("没有对应的处理请求！".getBytes(StandardCharsets.UTF_8));
            return;
        }
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        Method handlerMethod = handlerMapping.get(url);
        if (handlerMethod == null) {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.getOutputStream().write("没有找到该页面！".getBytes(StandardCharsets.UTF_8));
            return;
        }

        String requestType = request.getMethod();
        HandlerAdapter handlerAdapter = new SimpleHandlerAdapter();
        Object[] parameterValues = handlerAdapter.handle(request, response, handlerMethod);

        if ("GET".equals(requestType) && checkRequestMethod(handlerMethod, requestType)) {
            //GET请求调用方法
            Object result = invokeMethod(this.controllerMapping.get(url), handlerMethod, parameterValues);
            if (!Objects.isNull(result)) {
                response.getWriter().write(JSON.toJSONString(result));
            }
            return;
        } else if ("POST".equals(requestType) && checkRequestMethod(handlerMethod, requestType)){
            //POST调用方法
            Object result = invokeMethod(this.controllerMapping.get(url), handlerMethod, parameterValues);
            if (!Objects.isNull(result)) {
                response.getWriter().write(JSON.toJSONString(result));
            }
            return;
        }

        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.getOutputStream().write("不支持该类型！".getBytes(StandardCharsets.UTF_8));
        throw new RuntimeException("不支持的请求类型！" + requestType);
    }

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHandlerMapping() {
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
                    RequestMethod[] requestTypes = requestMapping.method();
                    String url = requestMapping.value();
                    url = (baseUrl + "/" + url).replaceAll("/+", "/");
                    //将对应处理的方法和控制器放入map中
                    handlerMapping.put(url, method);
                    controllerMapping.put(url, applicationContext.getBean(beanName, beanClass));
                    System.out.println(url + ": " + method.getName());
                }
            }
        }
    }

    private void loadConfig() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(DispatcherServlet.CONFIG_LOCATION);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
