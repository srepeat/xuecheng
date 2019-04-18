package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.manage_cms.service.PageService;
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

/**
 * 测试页面静态能否生成
 * @author 鲜磊 on 2019/4/1
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class GetPageHtmlTest {

    @Autowired
    private PageService pageService;

    @Test
    public void htmlTest(){
        String pageHtml = pageService.getPageHtml("5cb183d921f857279cd044d7");
        System.out.println(pageHtml);

    }

}
