package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


/**
 * 通过读取配置文件，进行某些配置
 */
public class PropertiesUtil {

    private static Logger logger= LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties properties;

    //静态代码块优于普通代码块，普通代码块优于构造器代码块（构造这个对象的时候都会执行一次）
    //执行并且仅在类加载的时候加载一次 在Tomcat启动的时候，会进行加载
    //Class.forName("com.mysql.jdbc.Driver")
    static
    {
        String fileName="mmall.properties";
        properties=new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e)
        {
            logger.error("配置文件读取异常",e);
        }
    }
    public  static String getProperty(String key)
    {
        String value=properties.getProperty(key.trim());
        if(StringUtils.isBlank(value))
        {
            return  null;
        }
        return  value.trim();
    }
    public  static String getProperty(String key,String defaultValue)
    {
        String value=properties.getProperty(key.trim());
        if(StringUtils.isBlank(value))
        {
            return  defaultValue.trim();
        }
        return  value.trim();
    }
}
