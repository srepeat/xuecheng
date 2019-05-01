package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 鲜磊 on 2019/5/1
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class SysDictionaryTest {

    @Autowired
    private SysDictionaryRepository sysDictionaryRepository;

    @Test
    public void testSys(){
        SysDictionary bydType = sysDictionaryRepository.findBydType("200");
        System.out.println(bydType);
    }
}
