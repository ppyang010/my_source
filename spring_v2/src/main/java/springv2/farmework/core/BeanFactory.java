package springv2.farmework.core;

public interface BeanFactory {

    /**
     * 这个我们知道 这个在spring中 是设置了init-lazy true 的bean 是会在这个方法中进行DI
     *
     * 从ioc容器中获取一个实例bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName);
}
