package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 鲜磊 on 2019/6/13
 **/
@Service
public class UserService {

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    private XcMenuMapper xcMenuMapper;

    //查询用户信息
    public XcUser findXcUserByUsername(String username){

        return xcUserRepository.findXcUserByUsername(username);
    }

    //根据账号查询用户的信息，返回用户扩展信息
    public XcUserExt getUserExt(String username){
        //查询用户
        XcUser xcUser = this.findXcUserByUsername(username);
        //如果等于null，返回一个null值
        if(xcUser == null){
            return null;
        }
        //获取userId
        String userId = xcUser.getId();

        //查询用户所有权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);

        //查询企业id
        XcCompanyUser companyUser = xcCompanyUserRepository.findByUserId(userId);

        String companyId = null;

        //判断是否为null
        if(companyUser != null){
            companyId = companyUser.getCompanyId();
        }

        //创建一个xcUserExt对象，设置企业id
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        //流拷贝
        xcUserExt.setCompanyId(companyId);
        //用户权限
        xcUserExt.setPermissions(xcMenus);
        //返回对象
        return xcUserExt;

    }
}
