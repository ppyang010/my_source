package springv2.farmework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * aop 代理类
 * 我们这里简单的只使用jdk的动态代理
 *
 * 这就要求 我们要被代理的对象都要实现
 *
 * @author ccy
 */
public class AopProxy implements InvocationHandler {

    private Object target;
    private AopConfig aopConfig;


    /**
     *
     * @param target 原生对象
     * @return
     */
    public Object getProxy(Object target){
        this.target = target;
        Class<?> clazz = this.target.getClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        if(interfaces!=null && interfaces.length>0){
            return Proxy.newProxyInstance(clazz.getClassLoader(), interfaces,this);
        }
        return target;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method m = this.target.getClass().getMethod(method.getName(),method.getParameterTypes());
        //这里进来的method是 代理对象的method 而map中的key是原始对象的method
        if( aopConfig != null && aopConfig.contains(m)){
            AopConfig.Aspect aspect = aopConfig.get(m);
            //调用aop的前置方法
            //todo 这里可以往aop通知类的前置方法传入参数  spring中切面类中通知方法的自带参数就是在这里传入的  封装方式 应该像springmvc 那边需要保存参数的顺序和类型
            aspect.getPoints()[0].invoke(aspect.getAspect());
        }
        Object res = null;
        try {
            res = method.invoke(target, args);
        } catch (InvocationTargetException e){
            throw e.getCause();
        }
        if(aopConfig != null && aopConfig.contains(m)){
            AopConfig.Aspect aspect = aopConfig.get(m);
            //调用aop的后置方法
            aspect.getPoints()[1].invoke(aspect.getAspect());
        }

        return res;
    }


    public void setAopConfig(AopConfig conifg){
        this.aopConfig = conifg;
    }

}
