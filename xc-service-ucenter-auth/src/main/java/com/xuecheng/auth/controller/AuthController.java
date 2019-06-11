package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 鲜磊 on 2019/6/11
 **/
@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {

    @Autowired
    AuthService authService;

    @Value("${auth.clientId}")
    String clientId;

    @Value("${auth.clientSecret}")
    String clientSecret;

    @Value("${auth.cookieDomain}")
    String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {

        //判断是否输入用户名和密码
        if(loginRequest == null || loginRequest.getUsername() == null){
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }

        if(loginRequest == null || loginRequest.getPassword() == null){
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }

        //用户名
        String username = loginRequest.getUsername();
        //密码
        String password = loginRequest.getPassword();

        AuthToken  authToken= authService.login(username,
                password, clientId, clientSecret);

        //将令牌写入co
        //访问token
        String access_token = authToken.getAccess_token();

        //将访问令牌存储到cookie
        saveCookie(access_token);

        return new LoginResult(CommonCode.SUCCESS,access_token);
    }

    //cookie
    private void saveCookie(String token){

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        //HttpServletResponse response,String domain,String path, String name,String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response,cookieDomain,"/","uid",token,cookieMaxAge,false);
    }


    @Override
    public ResponseResult logout() {
        return null;
    }
}
