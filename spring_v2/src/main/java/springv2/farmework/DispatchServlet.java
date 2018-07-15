package springv2.farmework;

import springv2.farmework.annotation.Controller;
import springv2.farmework.annotation.RequestMapping;
import springv2.farmework.context.MyApplicationContext;
import springv2.farmework.webmvc.HandlerAdapter;
import springv2.farmework.webmvc.HandlerMapping;
import springv2.farmework.webmvc.ModelAndView;
import springv2.farmework.webmvc.ViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 入口 只是作为一个mvc的入口
 * @author ccy
 */
public class DispatchServlet extends HttpServlet {


    private  final String LOCATION = "contextConfigLocation";


    //课后再去思考一下这样设计的经典之处
    //GPHandlerMapping最核心的设计，也是最经典的
    //它牛B到直接干掉了Struts、Webwork等MVC框架
    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    private Map<HandlerMapping,HandlerAdapter> handlerAdapters =new HashMap<>();

    private List<ViewResolver> viewResolvers = new ArrayList<>();


    /**
     *
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("say hello");
        this.doPost(req, resp);
    }

    /**
     *
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //mvc2 spring mvc 执行阶段 在spirng中是在dispatch 的doservice 方法中执行的
        try{
            doDispatch(req, resp);
        }catch (Exception e){
            resp.getWriter().write("<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","")
                    .replaceAll("\\s","\r\n") +  "<font color='green'><i>Copyright@GupaoEDU</i></font>");
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //取得处理当前请求的controller,这里也称为hanlder,处理器,
        //根据用户的url来判断获取
        HandlerMapping handler = getHandler(req);
        if(Objects.isNull(handler)){
            resp.getWriter().write("<font size='25' color='red'>404 Not Found</font><br/><font color='green'><i>Copyright@GupaoEDU</i></font>");
            return;
        }

        //获取处理request的处理器适配器handler adapter
        HandlerAdapter ha = getHandlerAdapter(handler);

        //实际的处理器处理请求,返回结果视图对象
        ModelAndView mv =ha.handler(req, resp, handler);

        //这一步才是真正的输出
        //在spring中还传入的req和handler  用于处理追踪异常
        processDispatchResult( resp, mv);
    }

    private void processDispatchResult( HttpServletResponse resp, ModelAndView mv) {
        //调用viewresolver的resolveview 方法
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        return null;
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        //分析  执行controller中 对应url的方法 需要哪些条件   1.url 2.对应的method方法 3.对应的controller对象
        //其中1可以从req获取  需要一个可以根据url 获取 method和对应controller集合的 封装对象 这个对象就是handlerMappin
        if(this.handlerMappings == null) { return  null;}

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+","/");

        //在初始化后的handlerMapping中匹配 对应的方法
        for (HandlerMapping handler : handlerMappings){
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){ continue; }
            return handler;
        }
        return null;
    }

    /**
     *
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        MyApplicationContext myApplicationContext = new MyApplicationContext(config.getInitParameter(LOCATION));
        //初始化springmvc 相关策略
        //mvc1
        initStrategies(myApplicationContext);

    }

    private void initStrategies(MyApplicationContext context) {
        //模拟springmvc 初始化
        /**
         * 有九种策略
         * 针对于每个用户请求,都会经过一些处理的策略之后,最终才能有结果输出
         * 每种策略可以自定义干预,但是最终的结果都是一致的
         * ModelAndView
         */
        //==========================这里就是spring mvc 九大组件
        /** 我们自己会实现*/
        initHandlerMappings(context);//通过handlerMappings 将请求映射到处理器
        initHandlerAdapters(context);//通过handlerAdapter进行多类型参数动态匹配
        initViewResolvers(context);//通过ViewResolver 解析逻辑视图到具体视图实现  如jsp,freemarker
        //------------下面的暂时不实现
//        initMultipartResolver(context);//文件上传解析
//        initLocaleResolver(context);//本地化解析
//        initThemeResolver(context);//主题解析
//        initHandlerExceptionResolvers(context);//执行时遇到异常处理
//        initRequestToViewNameTranslator(context);//直接解析请求到视图名
//        initFlashMapManager(context);//flash映射管理器
    }

    /**
     * 实现动态模板的解析
     * 自己简单模拟一套模板语言
     * @param context
     */
    private void initViewResolvers(MyApplicationContext context) {
        //假设请求 http://127.0.0.1/first.html
        //在这里需要解决 页面名字和模板文件关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        //绝对路径
        String templateRootPath =this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for(File template : templateRootDir.listFiles()){
            this.viewResolvers.add(new ViewResolver(template.getName(), template));
        }



    }

    /**
     *  handlerAdapters 用来动态匹配method参数,包括类转换,动态赋值
     * @param contex
     */
    private void initHandlerAdapters(MyApplicationContext contex) {
        //在初始化阶段,我们需要将这些参数的名字 或者类型 按一定顺序保存下来 (通过解析method 获取一个参数顺序)
        //后面用反射调用的时候参数只能传一个数组
        //后续可以根据这里记录的index 重新组装参数 正确的进行反射调用
        for(HandlerMapping handlerMapping : this.handlerMappings){
            //每一个方法有一个参数列表, 这里保存的是形参列表
            Map<String,Integer> paramMapping = new HashMap<>();


            //处理有注解的命名参数
            //因为一个形参会有多个注解  前一维是参数 后面是这个参数对应的注解数组
            Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
            for(int i = 0 ; i < pa.length; i++){
                for(Annotation a :pa[i]){
                    if(a instanceof RequestMapping){
                        String paraName = ((RequestMapping) a).value();
                        //这里如果没有赋值我们展示跳过
                        if(!"".equals(paraName.trim())){
                            paramMapping.put(paraName, i);
                        }
                    }
                }
            }

            //处理没有注解的参数非命名参数
            //只处理request 和 response  保存这两个参数在参数中的索引
            Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
            for(int i = 0; i < parameterTypes.length; i ++ ){
                Class<?> type = parameterTypes[i];
                if(type == HttpServletRequest.class ||
                        type == HttpServletResponse.class){
                    paramMapping.put(type.getName(), i);
                }
            }

            this.handlerAdapters.put(handlerMapping, new HandlerAdapter(paramMapping));


        }
    }

    /**
     * handlerMappings  用来保存controller 中配置的requestMapping 和 method 的一个对应关系
     * @param context
     */
    private void initHandlerMappings(MyApplicationContext context) {
        //这种对应关系 按我们的理解通常应该是一个map
        //map<String, method>   k url,v method
        //但是我们再代码中写url要支持正则 所以 获取到请求req中的url 也不能直接的调用map,get 还是要遍历  这样还不如 将urlmap和method controller 包装成一个对象 遍历这个对象集合进行匹配就好了'

        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for(String beanName : beanDefinitionNames){
            Object controller = context.getBean(beanName);
            Class<?> clazz = controller.getClass();

            if(!clazz.isAnnotationPresent(Controller.class)){continue;}

            String baseUrl = "";
            if(clazz.isAnnotationPresent(RequestMapping.class)){
                RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
                baseUrl = annotation.value();
            }

            //扫描所有的public方法
            Method[] methods = clazz.getMethods();
            for(Method method : methods){
                if(!method.isAnnotationPresent(RequestMapping.class)){ continue; }

                //这里简单模拟spring中的配置  简单的认为是正则  spring中使用的好像是spring的表达式
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
//                String regex = ("/" + baseUrl +requestMapping.value()).replace("/+","/");
                String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new HandlerMapping(pattern,controller,method));
                System.out.println("doInitHandlerMapping: " + regex + " , " + method);


            }


        }


    }


}

