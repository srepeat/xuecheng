package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 鲜磊 on 2019/6/16
 **/
@Component
public class LoginFilter extends ZuulFilter {

    @Autowired
    AuthService authService;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;//int值来定义过滤器的执行顺序，数值越小优先级越高
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //获取request上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = currentContext.getRequest();

        //查询cookie
        String cookie = authService.getTokenFromCookie(request);
        if(StringUtils.isEmpty(cookie)){
            //拒绝访问
            access_denied();
        }

        //查询header头
        String jwtFromHeader = authService.getJwtFromHeader(request);
        if(StringUtils.isEmpty(jwtFromHeader)){
            //拒绝访问
            access_denied();
        }

        //查询redis是否过期
        long expire = authService.getExpire(cookie);
        if(expire <= 0){
            //拒绝访问
            access_denied();
        }

        return null;
    }

    //拒绝访问
    private void  access_denied() {
        //获取request上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取response
        HttpServletResponse response = currentContext.getResponse();
        //取出头部信息Authorization
        //拒绝访问
        currentContext.setSendZuulResponse(false);
        //设置响应状态码
        currentContext.setResponseStatusCode(200);
        //响应结果集
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        //转化为JSON格式
        String jsonString = JSON.toJSONString(responseResult);
        //返回响应给页面
        currentContext.setResponseBody(jsonString);
        //返回类型
        response.setContentType("application/json;charset=utf-8");
    }

}
