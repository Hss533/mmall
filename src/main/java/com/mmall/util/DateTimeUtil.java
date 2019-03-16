package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;

public class DateTimeUtil {
    public static final  String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";
    public static Date strToDate(String dateTimeStr,String fromatStr)
    {
        org.joda.time.format.DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(fromatStr);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimeStr);
        return  dateTime.toDate();
    }
    public static String dateToStr(Date date,String formatStr)
    {
        if(date==null)
        {
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return  dateTime.toString(formatStr);
    }
    //重载
    public static Date strToDate(String dateTimeStr)
    {
        org.joda.time.format.DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimeStr);
        return  dateTime.toDate();
    }
    public static String dateToStr(Date date)
    {
        if(date==null)
        {
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return  dateTime.toString(STANDARD_FORMAT);
    }
}
