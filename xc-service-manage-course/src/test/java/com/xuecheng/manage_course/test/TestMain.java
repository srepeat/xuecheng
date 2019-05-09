package com.xuecheng.manage_course.test;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.CourseMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author 鲜磊 on 2019/4/27
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMain {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsPageClient cmsPageClient;

    @Test
    public void findById(){
        //服务ID
        String serverId = "XC-SERVICE-MANAGE-CMS";
        //HTTP调用
        ResponseEntity<Map> entity = restTemplate.getForEntity("http://"+serverId+"/cms/page/get/5cb183d921f857279cd044d7", Map.class);
        Map body = entity.getBody();
        System.out.println(body);
    }

    @Test
    public void findCmsPageById(){
        CmsPage cmsPage = cmsPageClient.findCmsPageById("5cb183d921f857279cd044d7");
        System.out.println(cmsPage);

    }



}
