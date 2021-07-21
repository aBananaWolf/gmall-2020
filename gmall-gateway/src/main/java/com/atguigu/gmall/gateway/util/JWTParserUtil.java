package com.atguigu.gmall.gateway.util;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.gateway.properties.JWTProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
