package springv2.farmework.context;

import springv2.farmework.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author ccy
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {
    //todo 这里的key 只使用了首字母小写 没有支持自定义
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    protected void refreshBeanFactory() {

    }
}
