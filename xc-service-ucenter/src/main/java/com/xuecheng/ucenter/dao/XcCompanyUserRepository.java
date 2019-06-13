package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 鲜磊 on 2019/6/13
 **/
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser,String> {

    //根据用户信息查询公司id
    XcCompanyUser findByUserId(String userId);
}
