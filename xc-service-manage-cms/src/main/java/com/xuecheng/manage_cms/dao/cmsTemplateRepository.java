package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 鲜磊 on 2019/4/1
 **/
public interface cmsTemplateRepository extends MongoRepository<CmsTemplate,String>  {


}
