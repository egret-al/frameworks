package web.servlet;

import com.spring.annotation.Controller;
import com.spring.config.BeanDefinition;
import com.spring.context.AnnotationConfigApplicationContext;
import com.spring.support.AbstractApplicationContext;
import web.annotation.RequestMapping;
import web.annotation.RequestMethod;
import web.annotation.RequestParam;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author：rkc
 * @date：Created in 2021/5/8 18:49
 * @description：
 */
@SuppressWarnings("serial")
public class DispatcherServlet extends HttpServlet {

    /* 用于加载配置文件 */
    private final Properties properties = new Properties();
    /* 自己手写的Spring IoC容器 */
    private AbstractApplicationContext applicationContext;
    /* 映射map，一个uri对应一个Method */
    private final Map<String, Method> handlerMapping = new ConcurrentHashMap<>(256);
    /* 映射控制器，一个uri对应一个Controller */
    private final Map<String, Object> controllerMapping = new ConcurrentHashMap<>(256);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        loadConfig(config.getInitParameter("contextConfigLocation"));
        String[] scanPackages = properties.getProperty("spring.scan-package").split(",");
        List<String> packageList = new ArrayList<>(Arrays.asList(scanPackages));
        //将扫描包加载到spring的上下文中
        applicationContext = new AnnotationConfigApplicationContext(packageList);
        initHandlerMapping();
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (handlerMapping.isEmpty()) {
            System.err.println("没有任何的处理器");
            response.getWriter().write("404 NOT FOUND!");
            return;
        }
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        Method handlerMethod = handlerMapping.get(url);
        if (handlerMethod == null) {
            response.getWriter().write("404 NOT FOUND!");
            return;
        }

        Object[] parameterValues = getParameterValues(request, response, handlerMethod);
        //调用方法
        Object result = handlerMethod.invoke(this.controllerMapping.get(url), parameterValues);
        if (!Objects.isNull(result)) {
            System.out.println("返回结果：" + result);
            response.getWriter().write(result.toString());
        }
    }

    private Object[] getParameterValues(HttpServletRequest request, HttpServletResponse response, Method method) {
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
            } else {
                throw new RuntimeException("存在参数没有标注RequestParam注解");
            }
        }
        return parameterValues;
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

    private void loadConfig(String location) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location);
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
