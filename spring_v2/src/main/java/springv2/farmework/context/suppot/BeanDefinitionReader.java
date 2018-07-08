package springv2.farmework.context.suppot;

import springv2.farmework.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对文件进行查找,读取,解析
 * @author ccy
 */
public class BeanDefinitionReader {
    //在配置文件中,用来获取自动是扫描的包名的key
    public static final String SCAN_PACKAGE = "scanPackage";
    private Properties config  = new Properties();


    //从配置文件中读取到的bean类名集合 (这里配置文件中使用的是扫描地址,所以是被扫描到的所有类的类名)
    private List<String> registerBeanClasses = new ArrayList<>();


    public BeanDefinitionReader(String ... locations) {
        //在Spring中是通过Reader去查找和定位
        //todo 这里简化操作只加载第一个 配置文件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        System.out.println("doLoadConfig");
        System.out.println(is);
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != is){is.close();}
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    /**
     * 注册bean
     * 每注册一个className,就返回一个BeanDefinition 我们自己包装这个
     * 只是为了对配置进行包装
     * @param className
     * @return
     */
    public BeanDefinition registerBeanDefinitions(String className){
        if (this.registerBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".") + 1)));
            return beanDefinition;
        }
        return null;
    }

//    public List<String> getRegisterBeanClasses(){
//        return  null;
//    }



    public List<String>  loadBeanDefinitions() {
        return registerBeanClasses;
    }


    public Properties getConfig() {
        return config;
    }

    /**
     * 递归扫描相关的class 便将其类名保存到一个list中
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
                registerBeanClasses.add(scanPackage + "." + file.getName().replace(".class",""));
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
