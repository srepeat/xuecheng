package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @author 鲜磊 on 2019/5/2
 **/
@Service
public class FileSystemService {


    @Autowired
    private FileSystemRepository fileSystemRepository;

    //日志
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemService.class);

    //配置文件拿去属性
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    private int connect_timeout_in_seconds;

    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    private int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    private String charset;
    @Value("${xuecheng.fastdfs.tracker_servers}")
    private String tracker_servers;


    //上传文件
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata){

        //判断文件不为空
        if(multipartFile == null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }

        String fileId = this.fdfs_upload(multipartFile);
        if(StringUtils.isEmpty(fileId)){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }

        //创建文件信息
        FileSystem fileSystem = new FileSystem();
        //文件id
        fileSystem.setFileId(fileId);
        //文件存储的路径
        fileSystem.setFilePath(fileId);
        //文件大小
        fileSystem.setFileSize(multipartFile.getSize());
        //业务标识
        fileSystem.setBusinesskey(businesskey);
        //标签
        fileSystem.setFiletag(filetag);
        //元信息 -->转换为JSON格式
        if(StringUtils.isEmpty(metadata)){
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        }
        //文件类型
        fileSystem.setFileType(multipartFile.getContentType());
        //名称
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        //保存
        fileSystemRepository.save(fileSystem);
        //返回结果集
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);

    }



    //上传文件到fastDFS上
    public String fdfs_upload(MultipartFile multipartFile){
        //获取配置文件
        this.configInit();
        //创建trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //获取连接
        try {
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storageServer
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //获取storageClient客户端
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storageServer);
            //上传文件方法
            byte[] bytes = multipartFile.getBytes();
            //原文件名称
            String originalFilename = multipartFile.getOriginalFilename();
            //截取后缀名
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
            String fileId = storageClient1.upload_file1(bytes, extName, null);
            return fileId;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    //初始化连接配置
    private void configInit(){
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.INITFDFSERROR);
        }
    }

}
