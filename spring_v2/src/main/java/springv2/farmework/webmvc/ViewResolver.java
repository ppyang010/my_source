package springv2.farmework.webmvc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        //这里写一个简陋的模板匹配  是否可以考虑直接使用模板引擎
        StringBuffer sb = new StringBuffer();

        RandomAccessFile ra = new RandomAccessFile(this.template, "r");
        String line = null ;
        while (null != (line = ra.readLine())){
            //对每行使用对应的正则进行匹配
//            line = new String(line.getBytes("ISO-8859-1"), "utf-8");
            Matcher m = matcher(line);
//            while (m.find()) {
                for(int i = 1 ;i<=m.groupCount(); i++){
                    //将 ${} 中间的字符取出来
                    String paramName = m.group(i);
                    Object paramValue = mv.getModel().get(paramName);
                    if(null == paramValue ){ continue; }
                    line = line.replaceAll("\\$\\{" + paramName + "\\}", paramValue.toString());
    //                line = new String(line.getBytes("utf-8"), "ISO-8859-1");
                }
//            }
            sb.append(line);
        }

        return sb.toString();
    }

    private Matcher matcher(String line) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        return  matcher;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getTemplate() {
        return template;
    }

    public void setTemplate(File template) {
        this.template = template;
    }
}
