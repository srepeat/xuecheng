package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 页面站点，主要获取页面物理路径
 * @author 鲜磊 on 2019/4/25
 **/
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {


}
