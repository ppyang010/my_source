package springv2.farmework.context;

import springv2.farmework.annotation.Autowired;
import springv2.farmework.annotation.Controller;
import springv2.farmework.annotation.Service;
import springv2.farmework.beans.BeanDefinition;
import springv2.farmework.beans.BeanPostProcessor;
import springv2.farmework.beans.BeanWrapper;
import springv2.farmework.context.suppot.BeanDefinitionReader;
import springv2.farmework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ccy
 */
public class MyApplicationContext  implements BeanFactory {
    private String [] configLocations;

    private BeanDefinitionReader reader;

    //todo 这里的三个map 的key使用的都是className  应该使用的是小写类名 或注解自定义的key

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    //使用注册式单例容器 存放单例bean
    //这里的key是className
    private Map<String,Object> beanCacheMap =new HashMap<>();

    //用来存储所有的被代理过的对象
    private Map<String,BeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

    public MyApplicationContext(String ... locations) {
        this.configLocations = locations;
        this.refresh();
    }

    /**
     * 模拟初始化入口
     */
    public void  refresh(){
        //定位  定位配置文件读取     (把配置文件中的所有类名读进来) 变成beanDefinition 注册到ioc容器中区
        this.reader = new BeanDefinitionReader(configLocations);
        //加载
        List<String> beanDefinitions = reader.loadBeanDefinitions();
        //注册
        doRegistry(beanDefinitions);

        // -----------------------------------------------------  上述是模拟ioc流程
        //依赖注入(lazy-init为false),要执行依赖注入
        //在这里自动调用getBean方法
        doAutowired();

        System.out.println();

    }


    /**
     * 自动化的依赖注入
     */
    private void doAutowired() {
        for(Map.Entry<String,BeanDefinition> entry : this.beanDefinitionMap.entrySet()){
            String beanName = entry.getKey();
            if(!entry.getValue().isLazyInit()){
                getBean(beanName);
            }

        }

    }

    public void populateBean(String beanName,Object instance){
        Class clazz = instance.getClass();

        //只支持这两个注解的bean进行依赖注入
        if(!(clazz.isAnnotationPresent(Controller.class)||clazz.isAnnotationPresent(Service.class))){ return; }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields){
            if(!field.isAnnotationPresent(Autowired.class)){
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);

            String autowiredBeanName = autowired.value();
            if("".equals(autowiredBeanName)){
                //根据类型
//                beanName = lowerFirstCase(field.getType().getSimpleName());
                    autowiredBeanName = field.getType().getName();

            }
            field.setAccessible(true);
            try {
                field.set(instance, this.beanWrapperMap.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 真正的将beanDefinitions 注册到ioc容器 (beanDefinitionsMap)
     * @param beanDefinitions
     */
    private void doRegistry(List<String> beanDefinitions) {
        //beanName 有三种情况
        //1.默认是类名首字母小写
        //2.自定义名字
        //3.接口注入
        try {
            for(String className : beanDefinitions){

                Class<?> beanClass = Class.forName(className);
                //如果是接口,不实例化
                //使用实现类来实例化 注册实现类
                if(beanClass.isInterface()){continue;}

                BeanDefinition beanDefinition = reader.registerBeanDefinitions(className);
                if(Objects.nonNull(beanDefinition) ){
                    this.beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition);
                }
                //如果这个类是接口的实现类 注册
                Class<?>[] interfaces = beanClass.getInterfaces();
                for(Class<?> i : interfaces){
                    //如果是多个实现类，只能覆盖
                    //为什么？因为Spring没那么智能，就是这么傻
                    //这个时候，可以自定义名字
                    this.beanDefinitionMap.put(i.getName(),beanDefinition);
                }

            }
            //到这里为止，容器初始化完毕
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过读取beanDefinition中的信息
     * 通过反射机制创建一个实例并返回
     * spring中的做法是, 不会吧最原始的对象放出去 会用一个beanwrapper 来进行一次包装
     * 包装器(装饰器)模式
     * 1,保留原来的oop管理
     * 2.可以对他进行扩展,增强(为了之后的AOP打基础)
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        //生成通知事件
        BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

        Object instance = instantiationBean(beanDefinition);
        if(Objects.isNull(instance)){ return null; }

        //在实例初始化以前调用一次
        beanPostProcessor.postProcessBeforeInitialization(instance,beanName);

        BeanWrapper beanWrapper = new BeanWrapper(instance);
        beanWrapper.setPostProcessor(beanPostProcessor);
        beanWrapperMap.put(beanName, beanWrapper);

        //在实例初始化以后调用一次
        beanPostProcessor.postProcessAfterInitialization(instance,beanName);

        //对实例化后的bean 进行依赖注入
        populateBean(beanName,instance);

        //通过这样一调用，相当于给我们自己留有了可操作的空间
        return this.beanWrapperMap.get(beanName).getWrapperInstance();
    }

    //传一个BeanDefinition，返回一个实例Bean
    private Object instantiationBean(BeanDefinition beanDefinition){
        Object  instance = null;
        String className = beanDefinition.getBeanClassName();
        //todo 这里要先判断是不是单例 这里我们就默认都是单例
        //使用注册式单例 存放单例的bean
        synchronized (this){
            try {
                if(this.beanCacheMap.containsKey(className)){
                    instance = this.beanCacheMap.get(className);
                }else {
                    Class<?> clazz = null;
                    clazz = Class.forName(className);
                    instance = clazz.newInstance();
                    this.beanCacheMap.put(className,instance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return instance;
    }
}
