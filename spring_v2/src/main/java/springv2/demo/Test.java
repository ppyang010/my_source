package springv2.demo;

import springv2.demo.action.MyAction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        String line = "<h1>大家好，我是${teacher}老师<br/>欢迎大家一起来探索Spring的世界</h1>";
        Matcher m = Test.matcher(line);
        while (m.find()){
            m.start();
            m.end();
            System.out.println(m.group());
            for (int i = 0 ; i <= m.groupCount(); i ++) {
                //要把￥{}中间的这个字符串给取出来
                String paramName = m.group(i);
                line = line.replaceAll("\\$\\{" + paramName + "\\}","Tom");
                System.out.println(line);
            }
        }
    }

    private static Matcher matcher(String line) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        return  matcher;
    }
}
