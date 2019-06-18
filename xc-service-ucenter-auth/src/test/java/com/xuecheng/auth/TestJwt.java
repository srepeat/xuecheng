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
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE1NjA5MDIyMTksImF1dGhvcml0aWVzIjpbInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYmFzZSIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfZGVsIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9saXN0IiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9wbGFuIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZSIsImNvdXJzZV9maW5kX2xpc3QiLCJ4Y190ZWFjaG1hbmFnZXIiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX21hcmtldCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfcHVibGlzaCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYWRkIl0sImp0aSI6IjZlMTg1NmY4LWNhZDQtNGIxZi04OWFiLWMwNDZkNjE1MjYyNyIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.HgG1T3khMDYyyaaFH-MpS0m1vkKkeWGrJJaRyiPcsUl1f_zCDSUXcfekzsuel27wr05LXjuUMaLluYHSwiG2cvC7S5pnPxtvzrJ5JAatYWDymir7MptJFyZ3_Xj07We98L7lgMPoqr0TizyAqrXojWBAWLSvWZIPgtqab28hOxgTZZzfpavFVTeB5aPzn18aHEjafe1Myru4umxGPMRKE2LZdQUqahTVBqn34eqrGt9kNn3lj69fQmMAHtluc37YDIN57j3mXMo6TzRlEGVaRa_WPNfa3OF0Msg1Cw4utWJWzSylG_g5qqNJOH6pg5L7uG14dg14tXt0NOh9R-oEvg";

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
