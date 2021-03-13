package com.spring.support;

import com.spring.annotation.*;
import com.spring.config.BeanDefinition;
import com.spring.factory.BeanNameAware;
import com.spring.factory.BeanPostProcessor;
import com.spring.factory.InitializingBean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author rkc
 * @date 2021/3/12 19:25
 */
public class DefaultListableBeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    private final List<BeanPostProcessor> beanPostProcessorList = new CopyOnWriteArrayList<>();

    public void registerBeanDefinition(Set<Class<?>> classSet) {
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Component.class)) {
                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                    //类实现了BeanPostProcessor接口
                    try {
                        BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                        beanPostProcessorList.add(beanPostProcessor);
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

        //基于Class去创建单例
        instanceSingletonBean();
    }

    private void instanceSingletonBean() {
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals(beanDefinition.SCOPE_SINGLETON)) {
                if (!singletonObjects.containsKey(beanName)) {
                    //创建bean
                    Object bean = doCreateBean(beanName, beanDefinition);
                    singletonObjects.put(beanName, bean);
                }
            }
        }
    }

    public Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Object bean = null;
        try {
            //1、实例化
            bean = beanClass.getDeclaredConstructor().newInstance();
            //2、依赖注入
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object o = getBean(field.getName());
                    field.setAccessible(true);
                    if (o != null) field.set(bean, o);
                }
            }
            //3、aware接口回调
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
            }
            //4、初始化
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessAfterInitialization(bean, beanName);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public Object getBean(String name) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition.getScope().equals(beanDefinition.SCOPE_SINGLETON)) {
            //单例池获取
            Object bean = singletonObjects.get(name);
            if (bean == null) {
                bean = doCreateBean(name, beanDefinition);
                singletonObjects.put(name, bean);
            }
            return bean;
        } else if (beanDefinition.getScope().equals(beanDefinition.SCOPE_PROTOTYPE)) {
            //原型
            return doCreateBean(name, beanDefinition);
        }
        return null;
    }

    @SuppressWarnings("all")
    public <T> T getBean(String name, Class<T> requiredType) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition.getScope().equals(beanDefinition.SCOPE_SINGLETON)) {
            //单例池获取
            return (T) singletonObjects.get(name);
        } else if (beanDefinition.getScope().equals(beanDefinition.SCOPE_PROTOTYPE)) {
            //原型
            return (T) doCreateBean(name, beanDefinition);
        }
        return null;
    }
}
