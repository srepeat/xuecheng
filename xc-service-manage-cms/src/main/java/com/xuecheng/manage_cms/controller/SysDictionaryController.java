package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.course.SysDicthinaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysdictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据字典控制层
 * @author 鲜磊 on 2019/5/1
 **/
@RestController
@RequestMapping("/sys/dictionary")
public class SysDictionaryController implements SysDicthinaryControllerApi{

    @Autowired
    private SysdictionaryService sysdictionaryService;

    @Override
    @GetMapping("/get/{type}")
    public SysDictionary findByType(@PathVariable("type") String type) {
        return sysdictionaryService.findBydtype(type);
    }
}
