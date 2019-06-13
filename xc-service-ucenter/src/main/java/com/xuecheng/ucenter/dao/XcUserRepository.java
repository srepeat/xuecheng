package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 鲜磊 on 2019/6/13
 **/
public interface XcUserRepository extends JpaRepository<XcUser,String> {

    //查询用户账号
    XcUser findXcUserByUsername(String username);
}
