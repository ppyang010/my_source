package springv2.farmework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class HandlerMapping {
    private Object controller;
    private Method method;
//    private String url;
    private Pattern pattern; //url的封装
    //todo
    //后续应该可以包含拦截器链

    public HandlerMapping(Pattern pattern,Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
