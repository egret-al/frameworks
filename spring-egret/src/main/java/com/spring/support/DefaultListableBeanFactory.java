package com.spring.support;

import com.spring.annotation.*;
import com.spring.config.BeanDefinition;
import com.spring.factory.BeanNameAware;
import com.spring.factory.BeanPostProcessor;
import com.spring.factory.InitializingBean;
import com.spring.factory.ObjectFactory;
import com.spring.resolver.*;
import com.spring.util.StringUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author rkc
 * @date 2021/3/12 19:25
 */
public class DefaultListableBeanFactory implements Serializable {

    /** 用于存放bean的定义信息 **/
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    /** 提前暴露的二级缓存 **/
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(256);
    /** 存放走完整个spring bean生命周期的对象 **/
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    /** 三级缓存 **/
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
    /** 如果对象正在创建中，则会加入到这个集合中 **/
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

    private static final Map<String, AnnotationParser> annotationPostProcessors = new ConcurrentHashMap<>(256);

    static {
        annotationPostProcessors.put("Component", new ComponentAnnotationParser());
        annotationPostProcessors.put("Service", new ServiceAnnotationParser());
        annotationPostProcessors.put("Repository", new RepositoryAnnotationParser());
        annotationPostProcessors.put("Controller", new ControllerAnnotationParser());
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.getBeanClass() == null) {
            throw new RuntimeException("非法的BeanDefinition");
        }
        synchronized (this.beanDefinitionMap) {
            this.beanDefinitionMap.put(beanName, beanDefinition);
        }
    }

    /**
     * 将字节码描述成一个BeanDefinition
     * @param classSet 字节码集合
     */
    public void registerBeanDefinition(Set<Class<?>> classSet) {
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class)
                    || clazz.isAnnotationPresent(Repository.class) || clazz.isAnnotationPresent(Controller.class)) {

                //如果实现了BeanPostProcessor接口，就添加到beanPostProcessors中
                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                    //类实现了BeanPostProcessor接口
                    try {
                        BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                        beanPostProcessors.add(beanPostProcessor);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                BeanDefinition beanDefinition = new RootBeanDefinition();
                beanDefinition.setBeanClass(clazz);

                Annotation annotation = clazz.getAnnotation(Component.class) == null ? clazz.getAnnotation(Service.class) : clazz.getAnnotation(Component.class);
                if (annotation == null) {
                    annotation = clazz.getAnnotation(Repository.class) == null ? clazz.getAnnotation(Controller.class) : clazz.getAnnotation(Repository.class);
                }
                if (annotation == null) {
                    throw new RuntimeException("注册beanDefinition期间发生异常");
                }

                //根据字节码取出对应的解析器
                AnnotationParser annotationParser = annotationPostProcessors.get(annotation.getClass().getInterfaces()[0].getSimpleName());
                String beanName = annotationParser.getBeanName(annotation);

                if (clazz.isAnnotationPresent(Scope.class)) {
                    Scope scope = clazz.getAnnotation(Scope.class);
                    beanDefinition.setScope(scope.value());
                } else {
                    //默认单例
                    beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
                }
                if (beanName.isEmpty()) {
                    beanName = StringUtils.toLowerCaseFirstOne(clazz.getSimpleName());
                }
                beanDefinitionMap.put(beanName, beanDefinition);
            }
        }
    }

    public void doCreateBean() {
        earlySingletonObjects.clear();
        singletonObjects.clear();
        singletonsCurrentlyInCreation.clear();
        singletonFactories.clear();

        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.isSingleton()) {
                if (!singletonObjects.containsKey(beanName)) {
                    Object bean = getSingleton(beanName);
                    //1、创建bean（调用构造方法），放入二级缓存中
                    if (bean == null) {
                        bean = createBeanInstance(beanName, beanDefinition);
                    }
                    //2、属性注入
                    populateBean(beanName, beanDefinition);
                    //3、接口回调BeanPostProcessor和InitializingBean
                    initializeBean(beanName, bean);
                }
            }
            //一个java对象走完整个spring生命周期后，移出二级缓存到一级缓存
            Object bean = earlySingletonObjects.remove(beanName);
            singletonObjects.put(beanName, bean);
            singletonsCurrentlyInCreation.remove(beanName);
        }
    }

    public void finishBeanFactoryInitialization() {
        this.earlySingletonObjects.clear();
        this.singletonObjects.clear();
        this.singletonsCurrentlyInCreation.clear();
        this.singletonFactories.clear();
        doCreateBean();
    }

    /**
     * 属性填充
     * @param beanName beanName
     * @param beanDefinition beanDefinition
     */
    public void populateBean(String beanName, BeanDefinition beanDefinition) {
        Object bean = singletonObjects.get(beanName);
        if (bean == null) {
            bean = earlySingletonObjects.get(beanName);
        }
        Field[] fields = beanDefinition.getBeanClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class) && field.getAnnotation(Autowired.class).required()) {
                String fieldName = field.getName();
                //TODO 修改属性注入逻辑
                if (field.isAnnotationPresent(Qualifier.class)) {
                    Qualifier qualifier = field.getAnnotation(Qualifier.class);
                    fieldName = qualifier.value();
                }
                //尝试从获取属性bean
                Object o = getSingleton(fieldName);
                if (o != null) {
                    //如果不为空，则直接填充
                    try {
                        field.setAccessible(true);
                        field.set(bean, o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 指定bean的名字返回单例对象
     * @param beanName bean的名字
     * @return spring bean
     */
    public Object getSingleton(String beanName) {
        Object singletonObject = this.singletonObjects.get(beanName);
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            return null;
        }

        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null) {
                    singletonObject = createBeanInstance(beanName, beanDefinition);
                }
                return singletonObject;
            }
        }
        if (singletonObject == null && beanDefinition.isSingleton()) {
            singletonObject = createBeanInstance(beanName, beanDefinition);
        }
        return singletonObject;
    }

    /**
     * 主要进行方法的回调，例如：BeanPostProcessor接口
     * @param beanName beanName
     * @param bean 对象
     * @return
     */
    @SuppressWarnings("all")
    public Object initializeBean(String beanName, Object bean) {
        //回调BeanPostProcessor的postProcessBeforeInitialization方法
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
        }

        //如果该bean实现了InitializingBean接口，就进行回调
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }

        //回调BeanPostProcessor的postProcessAfterInitialization
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            beanPostProcessor.postProcessAfterInitialization(bean, beanName);
        }
        return bean;
    }

    /**
     * 本质上就是利用反射调用构造方法
     * @param beanName beanName
     * @param beanDefinition beanDefinition
     * @return 普通Java对象，没有任何属性
     */
    public Object createBeanInstance(String beanName, BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Object bean = null;
        try {
            bean = beanClass.getDeclaredConstructor().newInstance();
            if (beanDefinition.isSingleton()) {
                //标注该对象正在创建中
                singletonsCurrentlyInCreation.add(beanName);
                this.earlySingletonObjects.put(beanName, bean);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public Object getBean(String name) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition == null) {
            return null;
        }
        if (beanDefinition.isSingleton()) {
            //单例池获取
            return getSingleton(name);
        } else if (beanDefinition.isPrototype()) {
            //原型
            return createBeanInstance(name, beanDefinition);
        }
        return null;
    }

    @SuppressWarnings("all")
    public <T> T getBean(String name, Class<T> requiredType) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition == null) {
            return null;
        }
        if (beanDefinition.isSingleton()) {
            //单例池获取
            return (T) getSingleton(name);
        } else if (beanDefinition.isPrototype()) {
            //原型
            return (T) createBeanInstance(name, beanDefinition);
        }
        return null;
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }
}
