package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 鲜磊 on 2019/4/1
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

//    通过ComponentSca扫描得组件注册到IOC容器中
//    注入mongoDb接口
    @Autowired
CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    //分页查询
    @Test
    public void testFindPage(){
        int page = 0; //从0开始查询
        int size = 10; //每页最大显示数
        Pageable pageRequest = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageRequest);
        System.out.println(all);
    }

    //根据ID查询
    @Test
    public void testFindById(){
        Optional<CmsPage> id = cmsPageRepository.findById("5a7be667d019f14d90a1fb1c");
        System.out.println(id);
    }


    //新增数据
    @Test
    public void testInsert(){
        CmsPage cmsPage = new CmsPage();
        //设置数据
        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        //使用集合
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }


    //修改
    @Test
    public void testUpdate(){
        /*Optional<CmsPage> optional = cmsPageRepository.findOne("5abefd525b05aa293098fca6");
        if (optional.isPresent()){
            CmsPage  cmsPage = optional.get();
            cmsPage.setPageName("测试页面");
            cmsPageRepository.save(cmsPage);
        }*/
    }

    //shanc
    @Test
    public void testDelete(){

        cmsPageRepository.deleteById("5ca213780cc6825ea0d0f5c1");
    }


    //根据类型查询
    @Test
    public void testTypeName(){
        CmsPage pageName = cmsPageRepository.findByPageName("index.html");
        System.out.println(pageName);
    }


    //条件查询
    @Test
    public void testByExample(){
        //分页
        int page = 0; //从0开始查询
        int size = 10; //每页最大显示数
        Pageable pageRequest = PageRequest.of(page, size);
        //自定义条件
        //创建对象
        CmsPage cmsPage = new CmsPage();
        //设置条件参数
        //cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        cmsPage.setPageAliase("课程");
        //设置条件匹配器,空的条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //可以设置查询类型，例：不区分大小写、包含、以...开始、以什么...结束

        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        //查询自定义条件以及分页
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageRequest);
        System.out.println(all);

    }

    //自定义条件查询测试
    @Test
    public void testFindAll01() {
        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",
                ExampleMatcher.GenericPropertyMatchers.contains());
        //页面别名模糊查询，需要自定义字符串的匹配器实现模糊查询
        //ExampleMatcher.GenericPropertyMatchers.contains() 包含
        //ExampleMatcher.GenericPropertyMatchers.startsWith()//开头匹配
        //条件值
        CmsPage cmsPage = new CmsPage();
        //站点ID
        //cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        //模板I
        //cmsPage.setTemplateId("5a962c16b00ffc514038fafd");
        cmsPage.setPageAliase("轮播图");
        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        Pageable pageable = new PageRequest(0, 10);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        System.out.println(all);
    }



}
