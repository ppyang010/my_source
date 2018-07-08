package springv2.farmework.beans;

/**
 *  ioc容器中实际存放的对象
 *  包含bean信息的对象
 *  存储配置文件中的信息(包含类关系之类的信息)   将配置文件中的信息统一的用beanDefinition的方式保存
 *  相当于保存在内存中的配置信息
 * 这个在spring中应该是接口这里简化一下
 * @author ccy
 */
public class BeanDefinition {

    private String beanClassName;
    private boolean lazyInit = false;
    //spring中获取bean的时候 如果是FactoryBean的实现类 会返回 FactoryBean.getObject() 否则就返回普通的bean
    //这里是bean在ioc中的名字
    private String factoryBeanName;

    public void setBeanClassName( String beanClassName){
        this.beanClassName =beanClassName;
    }



    public String getBeanClassName(){
        return beanClassName;
    }



    public void setFactoryBeanName( String factoryBeanName){
        this.factoryBeanName = factoryBeanName;
    }

    public String getFactoryBeanName(){
        return factoryBeanName;
    }


    public void setLazyInit(boolean lazyInit){
        this.lazyInit = lazyInit;
    }


    public boolean isLazyInit(){
        return lazyInit;
    }


    //todo 这里我们默认所有的类都是单例 降低复杂度

//    boolean isSingleton(){
//        return true;
//    }
//
//
//    boolean isPrototype(){
//        return  false;
//    }



}
