package com.atguigu.com.gmall.sso;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import io.jsonwebtoken.Jwt;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

@SpringBootTest
class GmallSsoApplicationTests {

    private String publicKeyStr = "G:\\23796\\rsa.pub";
    private String privateKeyStr = "G:\\23796\\rsa.pri";
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Test
    void contextLoads() throws UnsupportedEncodingException {
        try {
            RsaUtils.generateKey(publicKeyStr, privateKeyStr , "3w@repm-ql@qq");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    String token;

    @Test
    public void makeToken1() throws Exception{
        publicKey = RsaUtils.getPublicKey(publicKeyStr);
        privateKey = RsaUtils.getPrivateKey(privateKeyStr);
        token = JwtUtils.generateToken(new HashMap<String, Object>() {
            {
                this.put("id", "jpg");
                this.put("name", "Object?");
            }
        }, privateKey, 5);
        System.out.println(token);
    }

    @Test
    void parseToken() throws Exception{
        publicKey = RsaUtils.getPublicKey(publicKeyStr);
        long l = System.currentTimeMillis();
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoiT2JqZWN0PyIsImlkIjoianBnIiwiZXhwIjoxNTkzNjA2NjE1fQ.aa8KUeHwJpgw7KjN9o2PC6gTQazBxuENpLcHxVZlXUgBa-LCmtk26o3ctdv_19n-0ZhLesdp4kisji3UUbhkwlO-3inbWXOQ3WM6yKS9Q5Tu9gSk7jJ9zZNOZIIV_G7VWye9Q5g_3Q8Ny5BP_uYQEwuAhKnswjrCImekcJIjP8ySkEHIon4cWT0hHcCx7vnN49Ju5ufbHMoEkM2399OWCYlmikomC4-TV7ua28rDmYiE9MsMFWpsPibFJy1ApR8qDKt2pL-lTb-02Lgby21RmIONoNdOm0MZ2wCuSiVBZ0Gzue5D7Dp4xM6NG6cWjf3uymuI2i8UzgE23GsSgGPEZw";
        System.out.println(JwtUtils.getInfoFromToken(token, publicKey));
        System.out.println(System.currentTimeMillis() - l );
    }

    @Test
    public void makeToken() throws Exception {
        File file = new File("");
        System.out.println(file.getCanonicalPath());
    }

}
