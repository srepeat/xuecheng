package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
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

/**
 * @author 鲜磊 on 2019/6/6
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {


    @Test
    public void testCreateJwt(){
        //证书文件
        String key_location = "xc.keystore";

        //文件密码
        String keystore_password = "xuechengkeystore";

        //访问证书路径
        ClassPathResource pathResource = new ClassPathResource(key_location);
        //密钥工厂
        KeyStoreKeyFactory storeKeyFactory = new KeyStoreKeyFactory(pathResource,keystore_password.toCharArray());
        //密钥的密码，此密码和别名要匹配
        String keypassword = "xuecheng";
        //密钥别名
        String alias = "xckey";

        //密钥对
        KeyPair keyPair = storeKeyFactory.getKeyPair(alias, keypassword.toCharArray());
        //私钥
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        //定义payload信息
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "123");
        String toJSONString = JSON.toJSONString(tokenMap);
        //生成jwt令牌
        Jwt encode = JwtHelper.encode(toJSONString, new RsaSigner(rsaPrivateKey));
        //取出JWT令牌
        String encoded = encode.getEncoded();
        System.out.println(encoded);
    }


    @Test
    public void testVerify(){
        //JWT令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjEyMyJ9.DROrs7WMM88CWwjpXjTOjol-QW79cl6FZ17fL0rYvjk6KgGy2nWNVbp-5y-eOG9rbMxDcttZmqRfZr5hUHn454wOzX-XmI92U0OzdRwKOKv4PNx272I0GRdxEtKS3eTmg-tlKoB-1kaK4lViFmdcXMHF0PgjNvfdtY4hicCSX2dpJtkkon9PVFAft9cJYgAZJqEk2tcebj4vQ9hBrpxGMLXTt9XkkOikl97Xg_JdwW5Lu7CtcJuY3JvDhQvZDK3Vv-YxpvtzDjpyfwG9e79Hd-46hu5c5ZedBqEUfCYiBEWGaZfzpRGGBS-WDIjlsX2tdmlNPylYxVLovhP8M8iWLg";

        //公钥
        String keyPublic = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";

        //校验
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(keyPublic));

        //获取jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        System.out.println("---------------------");
        //JWT令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);

    }

}
