package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录、退出
 * @author 鲜磊 on 2019/6/11
 **/
@Service
public class AuthService {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;


    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //获取令牌
        AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
        if(authToken == null){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }

        //将Token存入redis
        String access_token = authToken.getAccess_token();
        //转为JSON格式
        String jsonString = JSON.toJSONString(access_token);

        //存储到redis
        boolean result = this.saveToken(access_token, jsonString, tokenValiditySeconds);
        //如果redis未开启或者其他原因就抛出异常
        if(!result){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        //返回对象
        return authToken;
    }

    //缓存到redis中
    private boolean saveToken(String access_token,String content,long ttl){
        //令牌名称
        String key = "user_token:" + access_token;

        stringRedisTemplate.boundValueOps(key).set(content,ttl, TimeUnit.SECONDS);

        //获取过期时间
        Long expire = stringRedisTemplate.getExpire(key);

        //时间大于0表示没有过期
        return expire>0;
    }



    //申请令牌
    private AuthToken applyToken(String username,String password,String clientId,String
            clientSecret){
        //采用客户端负载均衡，从eureka获取认证服务的ip 和端口
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //获取Url主机端口
        URI uri = serviceInstance.getUri();
        //申请令牌http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";

        //URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType
        // url就是 申请令牌的url /oauth/token
        //method http的方法类型
        //requestEntity请求内容
        //responseType，将响应的结果生成的类型
        //请求头两部分内容
        //1、header信息，包括了http basic认证信息
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String httpbasic = httpbasic(clientId, clientSecret);
        headers.add("Authorization",httpbasic);
        //2、包括：grant_type、username、password
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);

        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<>(body, headers);

        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode() !=400 && response.getRawStatusCode() !=401){
                    super.handleError(response);
                }
            }
        });

        //远程调用令牌
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, multiValueMapHttpEntity, Map.class);
        //获取参数
        Map exchangeBody = exchange.getBody();

        if(exchange == null ||
                exchangeBody.get("access_token") == null||
                exchangeBody.get("refresh_token") == null||
                exchangeBody.get("jti") == null){

            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);

        }

        AuthToken authToken = new AuthToken();
        authToken.setAccess_token((String) exchangeBody.get("jti"));//访问令牌(jwt)
        authToken.setJwt_token((String) exchangeBody.get("access_token")); //jti，作为用户的身份标识
        authToken.setRefresh_token((String) exchangeBody.get("refresh_token")); //刷新列表

        return authToken;
    }

    private String httpbasic(String clientId,String clientSecret){

        //将客户端id和客户端密码拼接，按“客户端id:客户端密码"
        String string = clientId +":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }


}
