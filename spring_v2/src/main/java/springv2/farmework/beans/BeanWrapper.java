package springv2.farmework.beans;


import springv2.farmework.aop.AopConfig;
import springv2.farmework.aop.AopProxy;
import springv2.farmework.core.FactoryBean;

/**
 * bean实现类的包装  实现在不影响用户定义的bean的状态下 进行扩展   如 bean的生命周期,和AOP
 * 这个在spring中应该是接口这里简化一下
 * @author ccy
 */
public class BeanWrapper extends FactoryBean {

    private AopProxy aopProxy  = new AopProxy();

    //还会用到观察者模式
    //1.支持事件响应,会有一个监听
    private BeanPostProcessor postProcessor;

    private Object wrapperInstance;

    //原始的通过反射new出来，要把包装起来，存下来
    private Object originalInstance;

    public BeanWrapper(Object object) {
        //todo 暂时两者相等 未以后aop做准备
        this.originalInstance = object;
        //2018年7月22日 进行aop改造  wrapperInstance 存放带代理后的对象
        this.wrapperInstance = aopProxy.getProxy(object);

    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    //返回代理后的class
    //可能会使这个$proxy0
    public Object getWrapperInstanceClass() {
        return wrapperInstance;
    }

    public Object getOriginalInstnace() {
        return originalInstance;
    }

    public BeanPostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(BeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    public void setAopConfig(AopConfig aopConfig) {
        aopProxy.setAopConfig(aopConfig);
    }
}
