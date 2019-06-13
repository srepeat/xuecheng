package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author 鲜磊 on 2019/6/6
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

    //负载均衡
    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;
    @Test
    public void testCreateRedis(){

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
        String httpbasic = httpbasic("XcWebApp", "XcWebApp");
        headers.add("Authorization",httpbasic);
        //2、包括：grant_type、username、password
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username","itcast");
        body.add("password","123");


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
        System.out.println(exchangeBody);
    }

    private String httpbasic(String clientId,String clientSecret){

        //将客户端id和客户端密码拼接，按“客户端id:客户端密码"
        String string = clientId +":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }


    //测试BCryptPasswordEncoder
    @Test
    public void testPasswrodEncoder(){

        //原生密码
        String password = "123456";

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        for (int i=0;i<=5;i++){
            String encode = bCryptPasswordEncoder.encode(password);
            System.out.println(encode);

            //验证密码
            boolean matches = bCryptPasswordEncoder.matches(password, encode);
            System.out.println(matches);
        }
    }



}
