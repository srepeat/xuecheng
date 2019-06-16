package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 验证JWT合法性
 * @author 鲜磊 on 2019/6/16
 **/
@Service
public class AuthService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    //获取cookie
    public String getTokenFromCookie(HttpServletRequest request){

        Map<String, String> readCookie = CookieUtil.readCookie(request, "uid");

        String uid = readCookie.get("uid");

        if(StringUtils.isEmpty(uid)){
            return null;
        }
        return uid;
    }

    //获取header头的Uid
    public String getJwtFromHeader(HttpServletRequest request){

        String authorization = request.getHeader("Authorization");

        if(StringUtils.isEmpty(authorization)){
            //拒绝访问
            return null;
        }

        if(!authorization.startsWith("Bearer ")){
        //拒绝访问
            return null;
        }
        
        //获取JWT令牌
        String jwt = authorization.substring(7);

        return jwt;
    }


    //redis查询
    public long getExpire(String access_token){
        //再token获取key
        String token = "user_token:" + access_token;

        //redis查询是否过期
        Long expire = stringRedisTemplate.getExpire(token);

        return expire;
    }


}
