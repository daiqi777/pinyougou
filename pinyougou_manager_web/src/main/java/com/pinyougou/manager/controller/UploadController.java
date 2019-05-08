package com.pinyougou.manager.controller;

import com.pinyougou.utils.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    @Value("${FAST_DFS_SERVICE_URL}")
    private String FAST_DFS_SERVICE_URL;

    @RequestMapping("upload")
    public Result upload(MultipartFile file){
        try {
            //1、图片原来的名字
            String oldName = file.getOriginalFilename();
            //2、获取后缀名，不带"."
            String extName = oldName.substring(oldName.lastIndexOf(".") + 1);
            //3、创建FastDFS客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
            //4、上传文件到FastDFS
            String fileId = fastDFSClient.uploadFile(file.getBytes(), extName);
            //5、拼接文件url
            String url = FAST_DFS_SERVICE_URL + fileId;
            //6、返回文件url
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }
}