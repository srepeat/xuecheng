package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 鲜磊 on 2019/4/1
 **/
public interface CmsPageRepository extends MongoRepository<CmsPage,String>  {

    //根据页面名称查询
    CmsPage findByPageName(String pageName);

    //根据页面名称、站点ID、页面路径查询
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String webPath);

}
