package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * 下载静态页面
 * @author 鲜磊 on 2019/4/26
 **/
@Service
public class PageService {

    //定义一个日志
    public static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;


    //将Html页面保存到物理路径下
    public void savePageToServerPath(String pageId){
        //使用页面id查询CmsPage
        CmsPage cmsPage = this.getCmsPageById(pageId);
        //得到html的文件id，从cmsPage中获取htmlFileId内容
        String htmlFileId = cmsPage.getHtmlFileId();
        //从gridFS中查询html文件
        InputStream inputStream = this.getFileById(htmlFileId);
        if(inputStream == null){
            LOGGER.error("getFileById InputStream is null ,htmlFileId:{}",htmlFileId);
            return;
        }
        //获取站点信息
        String siteId = cmsPage.getSiteId();
        //查询站点id
        CmsSite cmsSite = this.getCmsSiteById(siteId);
        //获取物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        //获取页面的物理路径(页面物理路径=站点物理路径+页面物理路径+页面名称)
        String pagePath = sitePhysicalPath +cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        //将html文件保存到服务器物理路径上
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    public InputStream getFileById(String fileId){

        //文件流对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //定义GridFsResource
        GridFsResource resource = new GridFsResource(gridFSFile,gridFSDownloadStream);

        try {
            return resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //查询页面id
    public CmsPage getCmsPageById(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(optional.isPresent()){
            //返回获取的对象
           return optional.get();
        }
        //没有就返回空
        return null;
    }

    //根据站点id进行查询
    public CmsSite getCmsSiteById(String pageId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(pageId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

}
