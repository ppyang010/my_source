package springv2.farmework.webmvc;

import java.io.File;

/**
 * 设计这个类的主要目的
 * 1.将一个静态文件变成动态文件
 * 2.根据用户参数不同,产生不同的结果
 * //最终输出字符串,交给response输出
 */
public class ViewResolver {


    private String viewName;
    private  File template;


    public ViewResolver(String viewName, File template){
        this.viewName = viewName;
        this.template = template;
    }

    public String viewResolver(ModelAndView mv) throws Exception{
        return null;
    }



}
