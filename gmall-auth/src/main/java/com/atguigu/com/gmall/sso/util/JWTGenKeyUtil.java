package com.atguigu.com.gmall.sso.util;

import com.atguigu.com.gmall.sso.properties.JWTProperties;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Map;

/**
 * @author wyl
 * @create 2020-07-01 20:19
 */
@Component
public class JWTGenKeyUtil {
    @Autowired
    private JWTProperties JWTProperties;

    public String genToken(Map<String, Object> map) {
        String token = null;
        try {
            token = JwtUtils.generateToken(map, JWTProperties.getPrivateKey(), JWTProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
}
