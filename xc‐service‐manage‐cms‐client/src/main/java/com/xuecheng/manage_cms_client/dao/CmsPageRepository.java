package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 查询页面信息
 * @author 鲜磊 on 2019/4/25
 **/
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
}
