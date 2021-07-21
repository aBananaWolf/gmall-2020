package com.atguigu.com.gmall.cart.util;

import com.atguigu.com.gmall.cart.entity.UserLoginInfo;
import com.atguigu.com.gmall.cart.properties.AnonymousProperties;
import com.atguigu.com.gmall.cart.properties.JWTProperties;
import com.atguigu.core.constant.TokenConstant;
import com.atguigu.core.utils.CookieUtils;
import com.sun.xml.internal.ws.resources.HttpserverMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author wyl
 * @create 2020-07-04 19:55
 */
@Component
public class UserLoginInfoUtil {
    @Autowired
    private AnonymousProperties anonymousProperties;
    @Autowired
    private JWTProperties jwtProperties;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    private final String USER_LOGIN_INFO = "userLoginInfo";

    public String getUserKey() {
        return CookieUtils.getCookieValue(request, anonymousProperties.getUserKeyName());
    }

    public void setUserKey(String value) {
        CookieUtils.setCookie(request, response, anonymousProperties.getUserKeyName(), value, anonymousProperties.getExpire());
    }

    public String getToken() {
        return CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
    }

    public void setUserLoginInfo(Object id, Object name) {
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setId(new Long (id.toString()));
        userLoginInfo.setName(name.toString());
        request.setAttribute(USER_LOGIN_INFO,userLoginInfo);
    }

    public void trySetUserLoginInfo(Map<String, Object> param) {
        Object idObj = param.get(TokenConstant.ID);
        Object nameObj = param.get(TokenConstant.NAME);
        if (idObj != null && nameObj != null)
            setUserLoginInfo(idObj, nameObj);
    }

    public UserLoginInfo getUserLoginInfo() {
        return (UserLoginInfo) request.getAttribute(USER_LOGIN_INFO);
    }
}
