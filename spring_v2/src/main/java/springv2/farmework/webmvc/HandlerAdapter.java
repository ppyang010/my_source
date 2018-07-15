package springv2.farmework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HandlerAdapter {


    //用于存放对应 handlerMapping 中方法的 参数名(key) 及其对应在参数列表中的index (value)
    private  Map<String, Integer> paramMpping;

    public HandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMpping =  paramMapping;
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
    public ModelAndView handler(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler) {
        //根据用户请求的参数信息，跟method中的参数信息进行动态匹配
        //resp 传进来的目的只有一个：只是为了将其赋值给方法参数，仅此而已
        //因为controller 方法参数可能有req和resp 这里就是为了传递

        //只有当用户传过来的 ModelAndView 为空时 才会 new 一个默认的
        return null;
    }
}
