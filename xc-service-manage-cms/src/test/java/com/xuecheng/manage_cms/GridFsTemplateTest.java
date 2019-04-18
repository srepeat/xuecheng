package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.Map;

/**
 * 测试GridFs增、查
 * @author 鲜磊 on 2019/4/1
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTemplateTest {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 测试GridFs测试存入数据
     * @throws FileNotFoundException
     */
    @Test
    public void testGridFs() throws FileNotFoundException {
        //要存储的文件
        File file = new File("d:/index_banner.ftl");
        //定义输入流
        FileInputStream inputStream = new FileInputStream(file);
        //向gridFs存储的文件
        ObjectId banner = gridFsTemplate.store(inputStream, "index_banner");
        //获得objectId
        String toString = banner.toString();
        //打印
        System.out.println(toString);
    }


    //查询下载数据
    @Test
    public void queryFile() throws IOException {
        String fileId = "5cb83fd86fe00e5a84b79797";
        //根据id查询   query查询,创建条件Criteria条件where.is(是什么)
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource资源，用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        //获取流数据  输入流
        String centen = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        //打印
        System.out.println(centen);
        //输入流
        InputStream inputStream = IOUtils.toInputStream(centen, "utf-8");
        //输出流
        File file = new File("e:/gridTest.html");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        //copy
        IOUtils.copy(inputStream,fileOutputStream);
        //关流
        inputStream.close();
        fileOutputStream.close();
    }

}
