import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class TEs {
    public static void main(String[] args) {

        boolean isSuccess=false;
        FTPClient ftpClient=new FTPClient();
        try{
            ftpClient.connect("39.105.84.39",21);
            isSuccess=ftpClient.login("ftpuser","533533");

        }catch (Exception e)
        {
            System.out.println("连接ftp服务器异常");
        }
        if (isSuccess==true)
        {
            System.out.println("ahh");
            System.out.println("连接ftp服务器正常");
        }
    }
}
