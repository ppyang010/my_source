package springv2.farmework;

import springv2.farmework.context.MyApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 入口 只是作为一个mvc的入口
 * @author ccy
 */
public class DispatchServlet extends HttpServlet {


    private  final String LOCATION = "contextConfigLocation";

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
        MyApplicationContext myApplicationContext = new MyApplicationContext(config.getInitParameter(LOCATION));
    }



}

