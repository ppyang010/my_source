package springv2.farmework.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 *  aop代理工具类
 *  判断是否为aop代理类
 *  获取代理类的被代理对象
 *
 * 在spring中 有对应的 AopProxyUtils 这个工具类
 *
 * @author ccy
 */
public class AopProxyUtils {

    public static  Object getTargetObject(Object proxy) throws Exception{
        //先判断一下，这个传进来的这个对象是不是一个代理过的对象
        //如果不是一个代理对象，就直接返回
        if(!isAopProxy(proxy)){ return proxy; }
        return getProxyTargetObject(proxy);
    }

    public static boolean isAopProxy(Object object){
        return Proxy.isProxyClass(object.getClass());
    }


    private static Object getProxyTargetObject(Object proxy) throws Exception{
        //jdk代理对象中有一个h属性存放原始对象
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field target = aopProxy.getClass().getDeclaredField("target");
        target.setAccessible(true);
        return  target.get(aopProxy);
    }

}
