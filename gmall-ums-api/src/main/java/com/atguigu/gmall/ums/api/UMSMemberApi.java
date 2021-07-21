package com.atguigu.gmall.ums.api;

import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("ums/member")
public interface UMSMemberApi {

    @PostMapping("/query")
    MemberEntity queryInfo(@RequestParam("username") String username, @RequestParam("password") String password);
}