package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
//等返回值msg和data的结果为null的时候，就可以只返回status了
//保证序列化json的时候如果是null的随想 key也会消失
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private  ServerResponse(int status)
    {
        this.status=status;
    }
    private  ServerResponse(int status,T data) {
        this.status = status;
        this.data=data;
    }
    private  ServerResponse(int status,String msg,T data) {
        this.status = status;
        this.msg=msg;
        this.data=data;
    }
    private  ServerResponse(int status,String msg) {
        this.status = status;
        this.msg=msg;
    }


    //使之不在Json序列化结果当中
    //不加public的都会在获取json
    //是否成功获取
    @JsonIgnore
    public boolean idSucsess()
    {
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    //创建一个对象通过一个成功的
    public static <T> ServerResponse<T> createBySuccess()
    {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> createBySuccessMessage(String msg)
    {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    //这样就不会出现调用
    public static <T> ServerResponse<T> createBySuccess(T data)
    {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    //传一个String作为一个data的时候 调用次函数
    public static <T> ServerResponse<T> createBySuccess(String msg,T data)
    {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createByError()
    {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage)
    {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }


    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage)
    {
        return new ServerResponse<T>(errorCode,errorMessage);
    }

}
