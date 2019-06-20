package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 拦截器实现远程携带JWT令牌
 * RequestInterceptor实现请求拦截器
 * @author 鲜磊 on 2019/6/19
 **/
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = requestAttributes.getRequest();

        //获取头上的JWT令牌
        Enumeration<String> headerNames = request.getHeaderNames();
        if(headerNames != null){
            while(headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                // 将header向下传递
                requestTemplate.header(headerName,headerValue);
            }
        }
    }
}
