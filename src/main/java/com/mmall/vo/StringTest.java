package com.mmall.vo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class A{
    private int id;
    private int age;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
public class StringTest {

    public static void main(String[] args) {

        //八大数据类型：int,double,long,short,char,float,byte,boolean
      /*  A test=new A();
        A test2=new A();
        System.out.println(test==test2);//false
        System.out.println(test.equals(test2));//false*/
        //两个都为false，因为equals的底层实现都是==
       //Integer a=new Integer(1);//false
      /*  Integer b=new Integer(2);//false
        System.out.println(a==b);
        System.out.println(a.equals(b));
        //自动拆箱 自动装箱
        int c2=1;
        int c3=1;
        System.out.println(c2==c3);
        Integer c=new Integer(129);//false
        Integer c1=new Integer(129);//true
        System.out.println(c==c1);
        System.out.println(c.equals(c1));

        Double d=new Double(1);*/

        /*Integer integer2=new Integer(1);
        Integer integer=new Integer(1);

        Integer integer1=Integer.valueOf(1);
        System.out.println(integer2==integer);//false
        System.out.println(integer.equals(integer2));//true

        System.out.println(integer.intValue());*/

        Double a=new Double(1.2);
        Double b=new Double(1.2);
        System.out.println(a==b);//false
        System.out.println(a.equals(b));//true

        double a1=1.2;
        double b1=1.2;//这个不能使用equals方法


        Double a2=Double.valueOf(1.2);
        Double b2=Double.valueOf(1.2);//直接创建两个对象
        System.out.println(a2==b2);//false
        System.out.println(a2.equals(b2));//true

        //short 的范围-32768~32767

        Long l=new Long(123);
        Long l2=new Long(123);
        System.out.println(l==l2);//false
        System.out.println(l.equals(l2));//true

        Long l3=Long.valueOf(128);
        Long l4=Long.valueOf(128);
        System.out.println(l3.equals(l4));//true
        System.out.println(l4==l3);//false l>-128 &&l<127

        /*
        public static Long valueOf(long l) {
            final int offset = 128;
            if (l >= -128 && l <= 127) { // will cache
                return Long.LongCache.cache[(int)l + offset];
            }
            return new Long(l);
        }
        *//*
        Byte byteNumber=new Byte(1);*/

        Float float1=Float.valueOf((float) 1.2);//创建一个新对象
        Float float2=Float.valueOf((float) 1.2);
        System.out.println(float1==float2);//false
        System.out.println(float1.equals(float2));//true

        //-128~127
        Short short1=Short.valueOf((short)-129);
        Short short2=Short.valueOf((short)-129);
        System.out.println(short1==short2);//true
        System.out.println(short1.equals(short2));//true

        //char 的包装类

    }

    @Test
    public void test()
    {
//        int,double,long,short,char,float,byte,boolean
        //float double这个的valueOf方法都是创建一个新的对象
        //long short integer byte这几个都是再-128~127之间使用缓存，其余的时候使用对象
        Byte byte1=Byte.valueOf((byte)-130);
        Byte byte2=Byte.valueOf((byte)126);
        System.out.println("嘤嘤嘤");
        System.out.println(byte1);
        System.out.println(byte2);
        System.out.println(byte1==byte2);//128 打印出来是-127 -129 打印出来是127
        System.out.println(byte1.equals(byte2));
        //-130打印出来是126

        //-128~127
        System.out.println(byte1==byte2);//true
        System.out.println(byte1.equals(byte2));//true

        int a=1;
        double doubel1=a;
        byte byte3=(byte)a;
        //强制转换的时候，小的可以转大的 ,大的转小的需要强制转换
        //向上转型？？？？

        Boolean b1=Boolean.valueOf(false);
        Boolean b2=Boolean.valueOf(false);
        System.out.println(b1.equals(b2));//true
        System.out.println(b1==b2);//true

        //char的包装类是什么？？？character
        //Char 自动装箱
        Character character=Character.valueOf('1');
        Character character1=Character.valueOf('1');
        System.out.println(character==character1);//true
        System.out.println(character.equals(character1));//true

    }
    @Test
    public void test1(){

        System.out.println('1'<127);
        String a="123";
        String b="123";
        String c=new String("123");
        String d=new String("123");
        System.out.println(a==b);//true
        System.out.println(a==c);//false
        System.out.println(a==d);//false

        System.out.println(b==c);//false
        System.out.println(b==d);//false

        System.out.println(c==d);//false

        String string1="12";
        String string2=new String("12");
        System.out.println(string1==string2);//false
        //如果这个涉及运算操作的话，就会转换为是判断值得相等
        Integer asd=null;
        //int asdf=asd;
        //System.out.println(asdf);//编译的时候会通过，但是运行的时候会产生异常

        Integer integer=2;
        int ints=2;
        System.out.println(integer==ints);//true


        int ints2=2;
        Integer integer2=2;
        System.out.println(integer2==ints2);//true


    }
    @Test
    public void test2()
    {
       /* Integer a=100;
        int b=200;
        Integer c=300;
        System.out.println(c==b+a);//true
        System.out.println( a instanceof Integer);*/
/*

        int a=100;
        int b=100;
        long c=200;
        System.out.println(a+b==c);//true
*/
/*
        Integer a=100;
        Integer b=100;
        Long c=200L;
        System.out.println(c==(a+b));
        System.out.println(c.equals(a+b));//false*/

        int a=1;
        Integer c=new Integer(1);
        Integer b=new Integer(1);
        System.out.println(a==c);//true
        System.out.println(a==b);//true
        System.out.println(b==c);//false

        int a1=1;
        Integer b1=1;
        Integer c1=1;
        System.out.println(a1==c1);//true
        System.out.println(a1==b1);//true
        System.out.println(c1==a1);//true

        Integer a2=1;
        Integer b2=new Integer(1);
        System.out.println(a2==b2);//false

    }

    @Test
    public void hashTest()
    {
        FanxingTest a=new FanxingTest();
        FanxingTest b=new FanxingTest();
        System.out.println(a.hashCode());//是一个native方法
        System.out.println(b.hashCode());

    }
    @Test
    public void collectiontest()
    {
        List<String> newList=new ArrayList<>();

        String a=new String("asd");
        newList.add(a);
        System.out.println(a.contains(new String("asd")));

        Set<String> stringSet=new HashSet<>();//不允许重复

        stringSet.add("123");
        stringSet.add(new String("123"));
        stringSet.add(new String("123"));
        System.out.println(stringSet.contains(new String("123")));


    }
    @Test
    public void jihe ()
    {
        HashSet<RectObject> set=new HashSet<>();
        RectObject r1 = new RectObject(3,3);
        RectObject r2 = new RectObject(5,5);
        RectObject r3 = new RectObject(3,3);
        set.add(r1);
        set.add(r2);
        set.add(r3);
        set.add(r1);
        System.out.println(r1.hashCode());
        System.out.println(r2.hashCode());
        System.out.println(r3.hashCode());
        System.out.println(r1==r2);
        System.out.println(r2==r3);
        System.out.println(r1==r3);
        //r1的hashCode和r3的hashCode
        //如果把hashcode()方法注释掉，那么r1,r2,r3就会添加进进去

        System.out.println("size:"+set.size());
        //将equals方法注释掉之后，r1,r2,r3也会添加进去


    }

}
class RectObject {
    public int x;
    public int y;
    public RectObject(int x,int y){
        this.x = x;
        this.y = y;
    }
    /*@Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }*/
    /*@Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final RectObject other = (RectObject)obj;
        if(x != other.x){
            return false;
        }
        if(y != other.y){
            return false;
        }
        return true;
    }*/
}
class Test1
{

    @Test
    public void test()
    {
        int a=1000;
        byte c=(byte)a;
        System.out.println(c);
    }

    public static void main(String[] args) {

        int a=1000;//默认是int类型
        byte c=(byte)a;
        System.out.println(c);
        long asd=a;
        double adada=2.3;//默认是double类型
        float ds=2.3f;
        //byte->short(char)->int->long->float->double;
        //这种叫隐式类型转换，也成为自动转换
        char cha='1';
        short we=(short)cha;
        short oi=67;
        char lo=(char)oi;
        System.out.println(we);//49
        System.out.println(lo);//C

        Byte hss=new Byte((byte)1);
        Byte hss2=Byte.valueOf((byte)1);
        System.out.println(hss);
        System.out.println(hss2);
        System.out.println(hss==hss2);
    }
}
