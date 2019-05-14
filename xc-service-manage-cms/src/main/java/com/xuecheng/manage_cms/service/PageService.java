package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.conﬁg.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.cmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * @author 鲜磊 on 2019/4/1
 **/
@Service
public class PageService {

    //注入cms接口
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private cmsTemplateRepository cmsTemplateRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    /**
     * 分页查询
     * @param page 分页的页码数
     * @param size 每页显示数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){
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
        //模板
        if(StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){

            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //别名
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){

            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //页面名称
        if(StringUtils.isNotEmpty(queryPageRequest.getPageName())){

            cmsPage.setPageName(queryPageRequest.getPageName());
        }
        //页面类型
        if(StringUtils.isNotEmpty(queryPageRequest.getPageType())){

            cmsPage.setPageType(queryPageRequest.getPageType());
        }

        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        //页面别名，绑定字段进行模糊查询
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        exampleMatcher = exampleMatcher.withMatcher("pageName",ExampleMatcher.GenericPropertyMatchers.contains());
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

    /*//新增页面
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
    }*/

    //新增页面
    public CmsPageResult add(CmsPage cmsPage) {
        //判断cms是否为空
        if(cmsPage == null){
            //抛出异常，非法请求
//            ExceptionCast.cast(CommonCode.SERVER_ERROR);
        }

        //1、校验是否有重复添加
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //2、进行判断不能为空
        if(cmsPage1 != null){
            //抛出可预知异常
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        //防止被注入主键id，我们设置id为null
        cmsPage.setPageId(null);
            //3、保存数据
        cmsPageRepository.save(cmsPage);
        //返回操作结果代码

        //操作失败
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
    }

    //根据id查询页面
    public CmsPage findById(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        //判断是否为空
        if(optional.isPresent()){
            //获取数据
            return optional.get();
        }
        //返回空
        return null;
    }

    //修改页面
    public CmsPageResult edit(String id,CmsPage cmsPage){
        //根据id查询是否存在
        CmsPage one = this.findById(id);
        //判空
        if(one != null){
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更行url信息
            one.setDataUrl(cmsPage.getDataUrl());
            //执行更新
            cmsPageRepository.save(one);
            //更新成功
            return new CmsPageResult(CommonCode.SUCCESS,one);
        }

        //更新失败
        return  new CmsPageResult(CommonCode.FAIL,null);

    }

    //删除方法
    public ResponseResult delete(String id){
        //查询是否存在，然后根据id删除
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        //判空
        if(optional.isPresent()){
            //执行删除
            cmsPageRepository.deleteById(id);
            //返回结果
            return new ResponseResult(CommonCode.SUCCESS);
        }
        //失败结果
        return new ResponseResult(CommonCode.FAIL);
    }

    /*
   1、静态化程序获取页面的DataUrl
   3、静态化程序远程请求DataUrl获取数据模型。
   4、静态化程序获取页面的模板信息
   5、执行页面静态化
   */
    //页面静态化
    public String getPageHtml(String pageId){
        //数据模型
        Map model = this.getModelByPageId(pageId);
        if(model == null){
            //获取数据模型为null
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面模板
        String templateByPageId = this.getTemplateByPageId(pageId);
        if(StringUtils.isEmpty(templateByPageId)){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = this.generateHtml(templateByPageId, model);
        if(StringUtils.isEmpty(html)){
            //静态化页面为null
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //返回静态化页面
        return html;
    }


    //执行静态化
    private  String generateHtml(String template,Map model){
        try {
            //模板配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            //模板加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template",template);
            //设置模板加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            //获取模板
            Template template1 = configuration.getTemplate("template");
            //静态化
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        } catch (Exception e) {e.printStackTrace();

        }

        return null;

    }


    //获取页面模板
    private String getTemplateByPageId(String pageId){
        //查询页面
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        //页面模板
        String templateId = cmsPage.getTemplateId();
        //判断页面是否为null
        if(StringUtils.isEmpty(templateId)){
            //页面模板为null
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        //根据模板id查询
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        //判断是否存在
        if(optional.isPresent()){
            CmsTemplate cmsTemplate = optional.get();
            //模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //取出模板文件内容
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            //获取流数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;

    }

    //获取数据模型
    private Map getModelByPageId(String pageId){
        CmsPage cmsPage = this.findById(pageId);
        //判断数据是否为空
        if(cmsPage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //获取dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl)){
            //dataUrl为null
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //获取dataUrl接口数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        //返回body数据
        Map body = forEntity.getBody();

        return body;
    }


    //页面保存
    public ResponseResult postPage(String pageId){
        //执行静态化
        String pageHtml = this.getPageHtml(pageId);
        //判断静态页面是否存在
        if(pageHtml ==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }

        //保存静态化文件
        CmsPage cmsPage = this.saveHtml(pageId, pageHtml);
        //发送消息
        this.sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //向页面发送信息
    private void sendPostPage(String pageId){
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //消息容器
        Map<String,Object> map = new HashMap<>();
        map.put("pageId",pageId);
        //消息内容
        String msg = JSON.toJSONString(map);
        //获取站点id作为routingKey
        String siteId = cmsPage.getSiteId();
        //发布消息
        this.rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,msg);

    }

    //保存静态化文件
    private CmsPage saveHtml(String pageId,String content){
        //查询页面
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(!optional.isPresent()){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //获取内容
        CmsPage cmsPage = optional.get();
        //先删除在进行保存
        String htmlFileId = cmsPage.getHtmlFileId();
        if(StringUtils.isNotEmpty(htmlFileId)){
            gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        }

       //查询pageId
        /*CmsPage cmsPage = this.findById(pageId);
        //参数非法
        if(cmsPage == null){
            //ExceptionCast.cast(CommonCode.INVALID_PARAM);
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }*/

        //保存文件加gridFs中
        InputStream inputStream = null;
        try {
            //转换为流
            inputStream = IOUtils.toInputStream(content.trim(),"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //保存到gridFs中
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        //文件id
        String fileId = objectId.toHexString();
        //将文件id存储到CmsPage中
        cmsPage.setHtmlFileId(fileId);
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }


    //判断页面是否存在，存在就更新，不存在就新增
    public CmsPageResult save(CmsPage cmsPage) {
        //判断页面是否存在
        String pageName = cmsPage.getPageName();
        String siteId = cmsPage.getSiteId();
        String pageWebPath = cmsPage.getPageWebPath();
        //查询
        CmsPage idAndPageWebPath = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(pageName, siteId, pageWebPath);
        //存在就添加
        if(idAndPageWebPath != null){
            //页面ID
            String pageId = idAndPageWebPath.getPageId();
            return this.edit(pageId,cmsPage);
        }

        //不存在就更新
        return this.add(cmsPage);
    }


    //一件发布
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //保存，是否存在
        CmsPageResult pageResult = this.save(cmsPage);
        if(!pageResult.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }

        //获取页面ID
        CmsPage cmsPage1 = pageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();

        //发布页面
        ResponseResult responseResult = this.postPage(pageId);
        if(!responseResult.isSuccess()){
           return new CmsPostPageResult(CommonCode.FAIL,null);
        }

        //获取页面url
        //站点id
        String siteId = cmsPage1.getSiteId();
        //查询站点信息
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        //页面Url= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
        String siteDomain = cmsSite.getSiteDomain();
        //物理路径
        String siteWebPath = cmsSite.getSiteWebPath();
        //页面路径
        String pageWebPath = cmsPage1.getPageWebPath();
        //页面名称
        String pageName = cmsPage1.getPageName();
        //URL信息
        String Url = siteDomain + siteWebPath + pageWebPath + pageName;
        //返回结果集
        return new CmsPostPageResult(CommonCode.SUCCESS,Url);

    }

    //查询站点id是否存在
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> siteOptional = cmsSiteRepository.findById(siteId);
        if(siteOptional.isPresent()){
            return siteOptional.get();

        }

        return null;
    }

}
