package com.ccy.springv1.spring;

import com.ccy.springv1.demo.action.MyAction;
import com.ccy.springv1.spring.annotation.Autowired;
import com.ccy.springv1.spring.annotation.Controller;
import com.ccy.springv1.spring.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 入口
 * @author ccy
 */
public class DispatchServlet extends HttpServlet {

    private Properties  contextConfig = new Properties();

    /**
     * 所有要实例化的类的类名集合
     */
    private List<String> classNameList = new ArrayList<>();

    /**
     * 模拟ioc容器
     */
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();
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

    }

    /**
     *
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //模拟容器初始化过程
        //定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //加载
        doScanner(contextConfig.getProperty("scanPackage"));
        System.out.println("classNameList:" + classNameList);
        //注册
        doRegistry();
        System.out.println("beanMap:" + beanMap);

        //依赖注入
        //自动依赖注入
        //在Spring中是通过调用getBean方法才出发依赖注入的
        doAutowired();
        MyAction myAction = (MyAction) beanMap.get("my");
        myAction.test();


        //如果是SpringMVC会多设计一个HnandlerMapping
        //farmework mvc  url 和 method 组成的map
        //将@RequestMapping中配置的url和一个Method关联上
        //以便于从浏览器获得用户输入的url以后，能够找到具体执行的Method通过反射去调用
        initHandlerMapping();

    }


    private void initHandlerMapping() {
        //这个功能在下个版本中实现
    }

    /**
     * 自动依赖注入
     */
    private void doAutowired() {
        if(beanMap.isEmpty()){
            return;
        }
        //遍历beanMap
        //获取bean的所有属性
        //遍历所有属性
        // 进行匹配并注入 (根据相同的bean name 策略到beanMap 中查找并赋值)
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields){
                if(!field.isAnnotationPresent(Autowired.class)){
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);

                String beanName = autowired.value();
                if("".equals(beanName)){
                    //根据类型
                    beanName = lowerFirstCase(field.getType().getSimpleName());
//                    beanName = field.getType().getName();

                }
                field.setAccessible(true);
                Object o = beanMap.get(beanName);
                try {
                    field.set(entry.getValue(), o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    /***
     * 根据之前在doScanner中获取到的类名进行实例化 并保存到ioc容器中
     * 注意 在spring中ioc容器中存放的是beanDefinition
     * 这里简化为直接存实例
     */
    private void doRegistry() {
        if(classNameList.isEmpty()){
            return;
        }
        try {

            for(String className : classNameList){
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(Controller.class)){
                    //默认用类名首字母注入
                    //如果自己定义了beanName，那么优先使用自己定义的beanName
                    //如果是一个接口，使用接口的类型去自动注入
                    Controller annotation = clazz.getAnnotation(Controller.class);
                    String beanName = annotation.value();
                    if("".equals(beanName)){
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }

                    beanMap.put(beanName, clazz.newInstance());

                }else if(clazz.isAnnotationPresent(Service.class)){
                    Service annotation = clazz.getAnnotation(Service.class);
                    String beanName = annotation.value();
                    if("".equals(beanName)){
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    beanMap.put(beanName, instance);

                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> i : interfaces){
                        beanName = lowerFirstCase(i.getSimpleName());
                        beanMap.put(beanName,instance);
                    }

                }else{
                    continue;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }



    /**
     * 根据包 将所有的class加载进来
     * @param scanPackage 目录
     */
    private void doScanner(String scanPackage) {
        //加载类需要类名 类名 包名.文件名
        //将所有类名用一个集合保存起来
        URL resource = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classDir = new File(resource.getFile());
        for(File file : classDir.listFiles()){
            if(file.isDirectory()){
                doScanner(scanPackage + "."+ file.getName());
            }else{
                classNameList.add(scanPackage + "." + file.getName().replace(".class",""));
            }
        }

    }

    /**
     * 这里主要是将配置文件读入
     * @param initParameter
     */
    private void doLoadConfig(String initParameter) {
        //在Spring中是通过Reader去查找和定位
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(initParameter.replace("classpath:",""));
        System.out.println("doLoadConfig");
        System.out.println(is);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != is){is.close();}
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * 首字母小写
     * @param str
     * @return
     */
    private String lowerFirstCase(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}

