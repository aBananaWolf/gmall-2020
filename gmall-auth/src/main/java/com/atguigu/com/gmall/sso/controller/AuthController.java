package com.atguigu.com.gmall.sso.controller;

import com.atguigu.com.gmall.sso.properties.JWTProperties;
import com.atguigu.com.gmall.sso.service.AuthService;
import com.atguigu.com.gmall.sso.util.JWTGenKeyUtil;
import com.atguigu.com.gmall.sso.util.JWTParserUtil;
import com.atguigu.core.constant.TokenConstant;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author wyl
 * @create 2020-07-03 11:18
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JWTGenKeyUtil JWTGenKeyUtil;
    @Autowired
    private JWTParserUtil parserUtil;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private HttpServletResponse httpServletResponse;
    @Autowired
    private JWTProperties jwtProperties;

    @PostMapping("/accredit")
    public String accredit(@RequestParam("username") String username, @RequestParam("password") String password) {
        MemberEntity memberEntity = authService.accredit(username,password);
        if (memberEntity == null)
            return null;
        String token = JWTGenKeyUtil.genToken(new HashMap<String, Object>() {
            {
                put(TokenConstant.ID, memberEntity.getId());
                put(TokenConstant.NAME, memberEntity.getUsername());
            }
        });
        // 应该设置二级域名，或者直接返回token(职责分离，只是签证，如果有其它系统接入，那么这才是符合需求的)
        CookieUtils.setCookie(httpServletRequest,httpServletResponse,jwtProperties.getCookieName(),token,jwtProperties.getCookieMaxAge());
        return token;
    }
}
