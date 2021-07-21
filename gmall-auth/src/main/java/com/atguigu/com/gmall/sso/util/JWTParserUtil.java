package com.atguigu.com.gmall.sso.util;

import com.atguigu.com.gmall.sso.properties.JWTProperties;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * @author wyl
 * @create 2020-07-03 11:25
 */
@Component
public class JWTParserUtil {
    @Autowired
    private JWTProperties jwtProperties;

    public Map<String, Object>  parseToken(String tokenStr) {
        Map<String, Object> token = null;
        try {
             token = JwtUtils.getInfoFromToken(tokenStr, jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }


}
