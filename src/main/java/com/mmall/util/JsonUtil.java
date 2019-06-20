package com.mmall.util;

import com.mmall.pojo.User;
import jdk.internal.org.objectweb.asm.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * jackson
 */
@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper=new ObjectMapper();
    static {
        //初始化objectMapper 对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //取消默认转换timestamps
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS,false);
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //将所有的日期格式都同一位一下的格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));


        //设置反序列话化 忽略在json 字符串中存在，但是在Java对象中不存在对应属性的情况 防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T> String obj2String(T obj){
        if(obj==null){
            return null;
        }
        try {

            return obj instanceof String ? (String)obj: objectMapper.writeValueAsString(obj);
        }
        catch (IOException e){
            log.warn("对象到String转换错误",e);
            return null;
        }
    }
    //可以返回一个格式化好的json字符串
    public static <T> String obj2StringPreety(T obj){
        if(obj==null){
            return null;
        }
        try {

            return obj instanceof String ? (String)obj: objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }
        catch (IOException e){
            log.warn("对象到String转换错误",e);
            return null;
        }
    }
    //把一个字符串转换为一个对象
    //第一个是声明这个是一个泛型方法 第二个是返回值是一个泛型
    public  static  <T>  T String2Obj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str)||clazz==null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str:objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            log.warn("将字符串转换为对象的时候error",e);
        return null;
        }
    }

    public  static <T> T string2Object(String str,
                                       org.codehaus.jackson.type.TypeReference<T> typeReference){
        if(org.apache.commons.lang.StringUtils.isNotEmpty(str)||typeReference== null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)?str:objectMapper.readValue(str,typeReference));
        }catch (Exception e){
            log.warn("",e);
            return null;
        }
    }
    public  static <T> T string2Object(String str,Class<T> collectionClass,Class<T>...elementClasses)
    {
        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        }catch (Exception e){
            log.warn("",e);
            return null;
        }
    }
    public static void main(String[] args) {
        User u1=new User();
        u1.setId(1);
        u1.setEmail("1097260937@qq.com");
        String userJson=JsonUtil.obj2String(u1);
        String userjsonpreety=JsonUtil.obj2StringPreety(u1);
        System.out.println(userJson);
        System.out.println(userjsonpreety);

        User user=JsonUtil.String2Obj(userJson,User.class);
        System.out.println(user.toString());
        //如果将user封装到list中的话，反序列话的话  就会不成功

    }
}
