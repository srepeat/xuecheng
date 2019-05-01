package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 鲜磊 on 2019/5/1
 **/
//@Repository
public interface SysDictionaryRepository extends MongoRepository<SysDictionary,String> {

    //根据数据字典类型来进行查询
    SysDictionary findBydType(String dtype);
}
