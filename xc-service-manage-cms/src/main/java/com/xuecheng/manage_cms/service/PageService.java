package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


/**
 * @author 鲜磊 on 2019/4/1
 **/
@Service
public class PageService {

    //注入接口
    @Autowired
    private CmsPageRepository cmsPageRepository;

    /**
     * 分页查询
     * @param page 分页的页码数
     * @param size 每页显示数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page,int size, QueryPageRequest queryPageRequest){
        //自定义条件查询
        //如果queryPageRequest等于null，就创建一个新的
        if(queryPageRequest == null){
            QueryPageRequest pageRequest = new QueryPageRequest();
        }
        //创建对象
        CmsPage cmsPage = new CmsPage();
        //设置模板、站点、别名
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){

            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }

        if(StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
           cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }

        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        //页面别名，绑定字段进行模糊查询
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //条件实例
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);


        //判断数据是否小于当前页码数
        if(page <= 0){
            page = 1;
        }
        //等同page - 1 = 0 还是0开始查询
            page = page-1;
        if(size <= 0){
            //每页显示10条数据
            size = 10;
        }
        //分页对象
        Pageable pageable = PageRequest.of(page, size);
        //分页查询
        Page<CmsPage> pages = cmsPageRepository.findAll(example,pageable);
        //封装结果集
        QueryResult queryResult = new QueryResult();
        queryResult.setList(pages.getContent()); //页码数
        queryResult.setTotal(pages.getTotalElements()); //分页数
        QueryResponseResult responseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);

        return responseResult;
    }

    //新增页面
    public CmsPageResult add(CmsPage cmsPage) {
        //1、校验是否有重复添加
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //2、进行判断不能为空
        if(cmsPage1 == null){
            //防止被注入主键id，我们设置id为null
            cmsPage.setPageId(null);
            //3、保存数据
            cmsPageRepository.save(cmsPage);
            //返回操作结果代码
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        //操作失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }


}
