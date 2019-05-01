package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 查询数据字典
 * @author 鲜磊 on 2019/5/1
 **/
@Service
public class SysdictionaryService {

    @Autowired
    private SysDictionaryRepository sysDictionaryRepository;

    //查询数据字典
    public SysDictionary findBydtype(String type){
       return sysDictionaryRepository.findBydType(type);
    }
}
