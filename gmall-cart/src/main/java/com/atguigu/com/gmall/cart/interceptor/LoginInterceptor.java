package com.atguigu.com.gmall.cart.interceptor;

import com.atguigu.com.gmall.cart.util.JWTParserUtil;
import com.atguigu.com.gmall.cart.util.UserLoginInfoUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * @author wyl
 * @create 2020-07-04 19:23
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserLoginInfoUtil userLoginInfoUtil;

    @Autowired
    private JWTParserUtil jwtParserUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 匿名用户
        String userKey = userLoginInfoUtil.getUserKey();
        if (StringUtils.isEmpty(userKey)) {
            userKey = UUID.randomUUID().toString();
            userLoginInfoUtil.setUserKey(userKey);
        }

        // 登录用户
        String tokenStr = userLoginInfoUtil.getToken();
        if (StringUtils.isNotEmpty(tokenStr)) {
            try {
                Map<String, Object> token = jwtParserUtil.parseToken(tokenStr);
                userLoginInfoUtil.trySetUserLoginInfo(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
