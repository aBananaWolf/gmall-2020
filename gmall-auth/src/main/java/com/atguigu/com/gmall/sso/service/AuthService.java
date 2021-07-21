package com.atguigu.com.gmall.sso.service;

import com.atguigu.gmall.ums.entity.MemberEntity;

/**
 * @author wyl
 * @create 2020-07-03 16:35
 */
public interface AuthService {
    MemberEntity accredit(String username, String password);
}
