package com.xuecheng.ucenter.test;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 鲜磊 on 2019/6/18
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMuen {


    @Autowired
    XcMenuMapper xcMenuMapper;


    @Test
    public void test(){
        String username = "49";
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(username);
        for (XcMenu xcMenu : xcMenus){
            System.out.println(xcMenu.getCode());
        }

    }
}
