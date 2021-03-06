package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * ftp工具类
 */
public class FTPUtil {
    private static final Logger logger= LoggerFactory.getLogger(FTPUtil.class);
    private static String ftpIp=PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass=PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;




    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public FTPUtil(String ip, int port, String user, String pwd){
        this.ip=ip;
        this.port=port;
        this.user=user;
        this.pwd=pwd;
    }
    public static boolean uploadFile(List<File> fileList) throws IOException
    {
        FTPUtil ftpUtil=new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result=ftpUtil.uploadFile("img",fileList);
        logger.info("开始连接ftp服务器，结束上传，上传结果{}",result);
        return result;
    }
    //这个remotePath是，linux的ftp服务器是一个文件夹，如果想在文件夹下的文件夹，就可以使用这个
    private boolean uploadFile(String remotePath,List<File> fileList) throws  IOException
    {
        boolean upload=true;
        FileInputStream fileInputStream=null;
        //连接ftp服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd))
        {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型为二进制文件 防止乱码
                ftpClient.enterLocalPassiveMode();// todo   打开本地的被动模式

                //上传多个文件
                for(File fileItem : fileList){
                    fileInputStream = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fileInputStream);
                }

            } catch (IOException e) {
                logger.error("上传文件异常",e);
                upload = false;
                e.printStackTrace();
            } finally {
                fileInputStream.close();
                ftpClient.disconnect();//释放连接
            }
        }else
            return false;
        return  upload;
    }
    private boolean connectServer(String ip,int port,String user,String pwd)
    {
        System.out.println("ip"+ip);
        System.out.println("user"+user);
        System.out.println("port"+port);
        System.out.println("pwd"+pwd);
        boolean isSuccess=false;
        ftpClient=new FTPClient();
        try{
            ftpClient.connect(ip,port);
            logger.info("userName="+user);
            logger.info("pwd"+pwd);
            isSuccess=ftpClient.login(user,pwd);

        }catch (IOException e)
        {
            logger.error("连接ftp服务器异常",e);
        }
        return  isSuccess;
    }
}
