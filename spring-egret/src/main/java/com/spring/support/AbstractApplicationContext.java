package com.spring.support;

import com.spring.config.BeanDefinition;
import com.spring.config.ConfigurableListableBeanFactory;
import com.spring.context.ConfigurableApplicationContext;
import com.spring.factory.BeanPostProcessor;
import com.spring.factory.NoSuchBeanDefinitionException;
import com.spring.factory.config.BeanFactoryPostProcessor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author：rkc
 * @date：Created in 2021/5/7 8:56
 * @description：
 */
public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {

    /* 当上下文确定后记录时间 */
    private long startUpDate;
    /* 表示当前上下文是否是活跃的 */
    private final AtomicBoolean active = new AtomicBoolean();
    /* 表示当前上下文是否已经被关闭 */
    private final AtomicBoolean closed = new AtomicBoolean();
    /* Bean工厂 */
    private final DefaultListableBeanFactory beanFactory;
    /* 存放所有实现了BeanFactoryPostProcessor的bean */
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    private final Object startupShutdownMonitor = new Object();

    public AbstractApplicationContext() {
        beanFactory = new DefaultListableBeanFactory();
    }

    public AbstractApplicationContext(DefaultListableBeanFactory beanFactory) {
        Objects.requireNonNull(beanFactory);
        this.beanFactory = beanFactory;
    }

    @Override
    public  <T> T getBean(String name, Class<T> requiredType) {
        return this.beanFactory.getBean(name, requiredType);
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }

    public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return this.beanFactory;
    }

    @Override
    public void refresh() throws IllegalStateException {
        synchronized (this.startupShutdownMonitor) {
            //准备刷新，记录下启动时间，标记已经启动
            prepareRefresh();

            //这里是提供给子类的扩展点，到这里的时候，所有的 Bean 都加载、注册完成了，但是都还没有初始化，具体的子类可以在这步的时候添加一些特殊的 BeanFactoryPostProcessor 的实现类或做点什么事
            postProcessBeanFactory(beanFactory);
            //调用 BeanFactoryPostProcessor 各个实现类的 postProcessBeanFactory(factory) 方法，此时所有的bean对象没有创建，只存在BeanDefinition
            invokeBeanFactoryPostProcessors(beanFactory);

            //初始化所有的 singleton beans
            finishBeanFactoryInitialization(beanFactory);
        }
    }

    public ClassLoader getClassLoader() {
        return AbstractApplicationContext.class.getClassLoader();
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        this.beanFactory.removeBeanDefinition(beanName);
    }

    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getBeanDefinition(beanName);
    }

    public BeanDefinitionRegistry getBeanDefinitionRegistry() {
        return this.beanFactory;
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }

    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        beanFactoryPostProcessors.clear();
        Map<String, BeanDefinition> beanDefinitions = beanFactory.getBeanDefinitions();
        //处理里面实现了BeanFactoryPostProcessor接口的BeanDefinition并回调
        for (String beanDefinitionKey : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanDefinitionKey);
            Class<?> beanClass = beanDefinition.getBeanClass();
            if (BeanFactoryPostProcessor.class.isAssignableFrom(beanClass)) {
                //该BeanDefinition实现了BeanFactoryPostProcessor接口
                try {
                    //此处同名key的beanFactoryPostProcessors里面的对象和DefaultListableBeanFactory里面最终的bean是不同的
                    this.beanFactoryPostProcessors.add((BeanFactoryPostProcessor) beanClass.getDeclaredConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        //进行回调
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessors) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory.finishBeanFactoryInitialization();
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    /**
     * 关闭上下文，摧毁工厂里面所有的bean
     */
    @Override
    public void close() {
        beanFactory.destroySingletons();
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    public long getStartUpDate() {
        return this.startUpDate;
    }

    protected void prepareRefresh() {
        this.startUpDate = System.currentTimeMillis();
        this.closed.set(false);
        this.active.set(true);
    }
}
