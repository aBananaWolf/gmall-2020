package com.atguigu.core.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    /**
     * 私钥加密token
     *
     * @param map           载荷中的数据
     * @param expireMinutes 过期时间，单位秒
     * @return
     * @throws Exception
     */
    public static String generateToken(Map<String, Object> map, PrivateKey key, int expireMinutes) throws Exception {
        return Jwts.builder()
                .setClaims(map)
                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(expireMinutes).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key, SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * 公钥解析token
     *
     * @param token  用户请求中的token
     * @return
     * @throws Exception
     */
    private static Jws<Claims> parserToken(String token, PublicKey key) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    /**
     * 获取token中的用户信息
     *
     * @param token  用户请求中的令牌
     * @return 用户信息
     * @throws Exception
     */
    public static Map<String, Object> getInfoFromToken(String token, PublicKey key) throws Exception {
        Jws<Claims> claimsJws = parserToken(token, key);
        return claimsJws.getBody();
    }

}