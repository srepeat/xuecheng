package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 测试文件上传
 * @author 鲜磊 on 2019/5/2
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDfs {

    private static final String FILE_ID = "group1/M00/00/00/wKgZhVzKkROAR4CrAAAawU0ID2Q752.png";
    //上传文件
    @Test
    public void uploadFile(){
        try {
            //加载属性配置文件fastdfs-client.properties
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建TrackerClient客户端
            TrackerClient trackerClient = new TrackerClient();
            //创建连接server
            TrackerServer connection = trackerClient.getConnection();
            //创建storageServer
            StorageServer storeStorage = trackerClient.getStoreStorage(connection);
            //创建存储客户端StorageClient1
            StorageClient1 storageClient1 = new StorageClient1(connection,storeStorage);
            //上传文件路径
            String filePath = "d:/logo.png";
            //返回文件Id
            String fileId = storageClient1.upload_file1(filePath, "png", null);
            System.out.println(fileId);
            //group1/M00/00/00/wKgZhVzKkROAR4CrAAAawU0ID2Q752.png
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //下载文件
    @Test
    public void downUpload(){
        try {
            //加载属性文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //tracker客户端
            TrackerClient trackerClient = new TrackerClient();
            //创建连接
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取StoreStorage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);

            //创建Storage客服端，传入连接和tracker服务
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //下载文件
            byte[] file1 = storageClient1.download_file1(FILE_ID);
            //写到本地磁盘
            OutputStream outputStream = new FileOutputStream(new File("d:/1.png"));
            outputStream.write(file1);
            //关流
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //文件查询
    @Test
    public void testQuery(){
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storageServer);
            //带有CRC32校验码1292373860
            FileInfo query_file_info1 = storageClient1.query_file_info1(FILE_ID);
            System.out.println(query_file_info1);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }


}
