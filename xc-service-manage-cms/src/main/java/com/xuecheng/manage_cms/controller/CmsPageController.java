package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 鲜磊 on 2019/4/1
 **/
@RestController
@RequestMapping("cms/page")
public class CmsPageController implements CmsPageControllerApi {

    //调用服务层
    @Autowired
    private PageService pageService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,@PathVariable("size") int size, QueryPageRequest queryPageRequest) {
        /*//封装结果集
        QueryResult queryResult = new QueryResult();
        //设置总页数
        queryResult.setTotal(2);
        //静态数据列表
        List list = new ArrayList<>();
        //创建cmsPage对象
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("测试页面");
        //添加到集合中
        list.add(cmsPage);
        queryResult.setList(list);

        //构造函数传递CommonCode.SUCCESS状态码 结果集
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);*/
        return pageService.findList(page,size,queryPageRequest);
    }

    @Override
    //讲对象转换为json格式
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }
}
