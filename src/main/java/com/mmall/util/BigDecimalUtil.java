package com.mmall.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
    private BigDecimalUtil()
    {

    }
    public static  BigDecimal add(double v1,double v2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }
    public static  BigDecimal jian(double v1,double v2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }
    public static  BigDecimal cheng(double v1,double v2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }
    public static  BigDecimal chu(double v1,double v2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//保留两位小数 使用四舍五入

    }

}
