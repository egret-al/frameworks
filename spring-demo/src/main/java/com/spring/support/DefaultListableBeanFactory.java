package com.spring.support;

import com.spring.annotation.*;
import com.spring.config.BeanDefinition;
import com.spring.factory.BeanNameAware;
import com.spring.factory.BeanPostProcessor;
import com.spring.factory.InitializingBean;
import com.spring.factory.ObjectFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author rkc
 * @date 2021/3/12 19:25
 */
public class DefaultListableBeanFactory extends DefaultSingletonBeanRegistry {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(256);
    protected final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
//    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

//    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

    /**
     * 将字节码描述成一个BeanDefinition
     * @param classSet 字节码集合
     */
    public void registerBeanDefinition(Set<Class<?>> classSet) {
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Component.class)) {
                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                    //类实现了BeanPostProcessor接口
                    try {
                        BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                        beanPostProcessors.add(beanPostProcessor);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                Component component = clazz.getAnnotation(Component.class);
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setBeanClass(clazz);
                String beanName = component.value();
                if (clazz.isAnnotationPresent(Scope.class)) {
                    Scope scope = clazz.getAnnotation(Scope.class);
                    beanDefinition.setScope(scope.value());
                } else {
                    //默认单例
                    beanDefinition.setScope(beanDefinition.SCOPE_SINGLETON);
                }
                if (beanName.isEmpty()) {
                    beanName = clazz.getSimpleName();
                }
                beanDefinitionMap.put(beanName, beanDefinition);
            }
        }
    }

    private void doCreateBean() {
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals(beanDefinition.SCOPE_SINGLETON)) {
                if (!singletonObjects.containsKey(beanName)) {
                    Object bean = getSingleton(beanName);
                    //1、创建bean（调用构造方法），放入二级缓存中
                    if (bean == null) bean = createBeanInstance(beanName, beanDefinition);

                    //2、属性注入
                    populateBean(beanName, beanDefinition);
                    //3、接口回调BeanPostProcessor和InitializingBean
                    initializeBean(beanName, bean);
                }
            }
            //一个java对象走完整个spring生命周期后，移出二级缓存到一级缓存
            Object bean = this.earlySingletonObjects.remove(beanName);
            this.singletonObjects.put(beanName, bean);
        }
    }

    public void finishBeanFactoryInitialization() {
        doCreateBean();
    }

    /**
     * 属性填充
     * @param beanName beanName
     * @param beanDefinition beanDefinition
     */
    public void populateBean(String beanName, BeanDefinition beanDefinition) {

        Object bean = singletonObjects.get(beanName);
        if (bean == null) bean = earlySingletonObjects.get(beanName);
        Field[] fields = beanDefinition.getBeanClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                //尝试从获取属性bean
                Object o = getSingleton(field.getName());
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
        //能够直接获取到singleton（存在singletonObjects中的spring bean）
        if (singletonObject != null) return singletonObject;
        //不存在，尝试从earlySingletonObjects中获取bean
        singletonObject = this.earlySingletonObjects.get(beanName);
        if (singletonObject != null) return singletonObject;

        Object instance = createBeanInstance(beanName, this.beanDefinitionMap.get(beanName));
        earlySingletonObjects.put(beanName, instance);
//        synchronized (this.singletonObjects) {
//            singletonObject = this.singletonObjects.get(beanName);
//            if (singletonObject == null) {
//                singletonObject = this.earlySingletonObjects.get(beanName);
//                if (singletonObject == null) {
//                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
//                    if (singletonFactory != null) {
//                        singletonObject = singletonFactory.getObject();
//                        this.earlySingletonObjects.put(beanName, singletonObject);
//                        this.singletonFactories.remove(beanName);
//                    }
//                }
//            }
//        }
        return instance;
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
     * @return
     */
    public Object createBeanInstance(String beanName, BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Object bean = null;
        try {
            //1、实例化完成后，放入二级缓存
            bean = beanClass.getDeclaredConstructor().newInstance();
            this.earlySingletonObjects.put(beanName, bean);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public Object getBean(String name) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition.getScope().equals(beanDefinition.SCOPE_SINGLETON)) {
            //单例池获取
            return getSingleton(name);
        } else if (beanDefinition.getScope().equals(beanDefinition.SCOPE_PROTOTYPE)) {
            //原型
            return createBeanInstance(name, beanDefinition);
        }
        return null;
    }

    @SuppressWarnings("all")
    public <T> T getBean(String name, Class<T> requiredType) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition.getScope().equals(beanDefinition.SCOPE_SINGLETON)) {
            //单例池获取
            return (T) getSingleton(name);
        } else if (beanDefinition.getScope().equals(beanDefinition.SCOPE_PROTOTYPE)) {
            //原型
            return (T) createBeanInstance(name, beanDefinition);
        }
        return null;
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }
}
