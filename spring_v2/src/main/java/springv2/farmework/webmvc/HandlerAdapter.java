package springv2.farmework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

public class HandlerAdapter {


    //用于存放对应 handlerMapping 中方法的 参数名(key) 及其对应在参数列表中的index (value)
    private  Map<String, Integer> paramMapping;

    public HandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMapping =  paramMapping;
    }

    /**
     *  请求中的参数转义成 controller method 的参数
     *  因为请求中的参数只会是string 而method参数可以是对象之类的
     *
     * @param req  用于获取请求参数 和传递到controller 中的method中去
     * @param resp 传递到controller 中的method中去
     * @param handler 用于获取method 和 controller
     * @return
     */
    public ModelAndView handler(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler) throws InvocationTargetException, IllegalAccessException {
        //根据用户请求的参数信息，跟method中的参数信息进行动态匹配
        //resp 传进来的目的只有一个：只是为了将其赋值给方法参数，仅此而已
        //因为controller 方法参数可能有req和resp 这里就是为了传递
        //controller形参中的modelAndView 也是在这里赋值的  不过这里没有在初始化的时候记录mv所在的参数序号所以暂时实现这点

        //只有当用户传过来的 ModelAndView 为空时 才会 new 一个默认的

        //1.准备这个方法的形参列表
        //方法重载：形参的决定因素：参数的个数、参数的类型、参数顺序、方法的名字
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();

        //2.获取请求参数
        Map<String, String[]> parameterMap = req.getParameterMap();

        //3.构造实参列表
        Object[] paramValues = new Object[parameterTypes.length];
        for(Map.Entry<String,String[]> entry : parameterMap.entrySet()){
            //这里优化了视频中的代码
            String value = String.join(",", entry.getValue());

            if(!this.paramMapping.containsKey(entry.getKey())){ continue;}
            Integer index = this.paramMapping.get(entry.getKey());

            //从请求中获取的参数均为string类型 方法中定义的形参列表比较的分布需要进行类型转换
            //在spring中有许多的转换器  并且可以自定义转换器
            //这里只将string 转化为  基本类型  复杂的自定义类型暂不考虑
            paramValues[index] = caseStringValue(value,parameterTypes[index]);
        }
        if(this.paramMapping.containsKey(HttpServletRequest.class.getName())){
            Integer reqIndex = this.paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if(this.paramMapping.containsKey(HttpServletResponse.class.getName())) {
            Integer respIndex = this.paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }


        //4.从handler中取出controller \method , 然后利用反射机制进行调用

        Object result = handler.getMethod().invoke(handler.getController(), paramValues);
        if(Objects.isNull(result)){ return null;}
        boolean isModelAndView = handler.getMethod().getReturnType() == ModelAndView.class;
        if(isModelAndView){
            return (ModelAndView) result;
        }
        return null;
    }


    private Object caseStringValue(String value,Class<?> clazz){
        if(clazz == String.class){
            return value;
        }else if(clazz == Integer.class){
            return  Integer.valueOf(value);
        }else if(clazz == int.class){
            return Integer.valueOf(value).intValue();
        }else {
            return null;
        }
    }




}
