package com.atguigu.com.gmall.sso.service.impl;

import com.atguigu.com.gmall.sso.api.UMSMemberApiService;
import com.atguigu.com.gmall.sso.service.AuthService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wyl
 * @create 2020-07-03 16:48
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UMSMemberApiService memberApiService;

    @Override
    public MemberEntity accredit(String username, String password) {
        return memberApiService.queryInfo(username,password);
    }
}
