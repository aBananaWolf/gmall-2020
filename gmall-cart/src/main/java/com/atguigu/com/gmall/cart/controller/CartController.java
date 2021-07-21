package com.atguigu.com.gmall.cart.controller;

import com.atguigu.com.gmall.cart.entity.UserLoginInfo;
import com.atguigu.com.gmall.cart.util.UserLoginInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wyl
 * @create 2020-07-04 19:58
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private UserLoginInfoUtil loginInfoUtil;

    @RequestMapping("/test")
    public Object test() {
        UserLoginInfo userLoginInfo = loginInfoUtil.getUserLoginInfo();
        return userLoginInfo;
    }
}
