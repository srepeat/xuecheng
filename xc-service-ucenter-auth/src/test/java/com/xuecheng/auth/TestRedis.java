package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 鲜磊 on 2019/6/6
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void testCreateRedis(){
        //key
        String key = "user_token:7970654f-8aff-4ae8-8766-6f7112efdf45";
        //value使用JSON格式
        Map<String,String> map = new HashMap<>();
        map.put("access_token","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU2MDEzMTYxOSwianRpIjoiNzk3MDY1NGYtOGFmZi00YWU4LTg3NjYtNmY3MTEyZWZkZjQ1IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.POKP_oxmk3oJiZCmN3me1E8wTXCG2bfDELJuPjjCREcB8_u0tBhGmVfua_7aOx3hwfTwBBnAw_q0J5Yf9-0N5xYvykCVx28Jry3Wl5ESg7XcxbZKFNp1-V_1GDaGXMsS9-r9M7hUf4vSi0Yvd-jsCEAYs4r4b_v6CBZuUcGTclucS677OgfvFTf-t4tS-iTDs7UwPiOU9CMG5V_mL7Pf3Ux5k_5zYr687aBWqK9o_1607KpqO9nf_hEoOk91MsbT7GDeFKIw9vM8tQ4_SNK7aq7hhhaowNHzmelu8Z2zV3JYixzVsmStwHwvx1iINRWJQTIzZQ7DTNehQz98IBgZ5g");
        map.put("refresh_token","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiI3OTcwNjU0Zi04YWZmLTRhZTgtODc2Ni02ZjcxMTJlZmRmNDUiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU2MDEzMTYxOSwianRpIjoiNWNmYWIxMTEtYTQ3My00ZTdkLTg5OTctNGI4YmE4MjhlMTcxIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.AAU_x-fchehJVrztxPDrfbZoL6PQXZuORQ5vxaz9NR7CHd2qudo78vqJkgkzxcV_EbWxrqywOixvQ7OUBR7fb-7Ewk5RPPZsuianWank5EqntlR4WP8i9gZm7H7j8-oXQlgd2DZU788Ts-C7sqOBLhx-p_6O-Y5SOcisfcyvgqnOHYGouCGzyqJn3S2w2ugSUROKzDGFgqpK6MHIwbd9zEr57Ud0Vzbr4z-An36Dt0HSki-7CTMQlVQocv8MR3gya96BVJ83WKE3N4-ZZ5sJSJGSSeX-d2o7t2LFNyoCaC-5YL2Ib8BlSSHm-jDhJ2RRyFg4KVRqJNJ8X_U5symFCA");

        //转成JSON字符串
        String jsonString = JSON.toJSONString(map);

        stringRedisTemplate.boundValueOps(key).set(jsonString,120, TimeUnit.SECONDS);

        //获取Key得参数,过去返回-2
        Long expire = stringRedisTemplate.getExpire(key);
        System.out.println(expire);

        //根据Key获取
        String s = stringRedisTemplate.opsForValue().get(key);
        System.out.println(s);
    }


}
