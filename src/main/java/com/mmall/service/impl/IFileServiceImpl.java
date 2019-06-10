package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

@Service("iFileService")
public class IFileServiceImpl  implements IFileService
{
    org.slf4j.Logger logger= LoggerFactory.getLogger(IFileServiceImpl.class);
    public String upload(MultipartFile file,String path)
    {
        String fileName=file.getOriginalFilename();
        //获取文件扩展名
        String fileNameExtension=fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName= UUID.randomUUID().toString()+"."+fileNameExtension;
        logger.info("开始上传文件，上传文件的文件名:{},上传的路径{},上传的新的文件名:{}",fileName,path,uploadFileName);

        File fileDir=new File(path);
        if(!fileDir.exists())//没有存在的话创建其文件夹
        {
            fileDir.setWritable(true);//赋予其写的权利
            fileDir.mkdirs();
        }

        //新的文件
        File targetFile=new File(path,uploadFileName);
        try{
            file.transferTo(targetFile);
            //文件已经上传成功了
            //todo 将targetFile上传到我们的ftp服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));//使用gava缓存
            //已经上传到服务器上了
            //todo 上传完之后，删除upload下的文件
            //targetFile.delete();


        }catch (IOException e)
        {
            logger.error("上传文件异常",e);
            return  null;
        }
        return  targetFile.getName();
    }
}
