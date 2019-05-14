package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**查询Site表
 * @author 鲜磊 on 2019/4/1
 **/
public interface CmsSiteRepository extends MongoRepository<CmsSite,String>  {


}
