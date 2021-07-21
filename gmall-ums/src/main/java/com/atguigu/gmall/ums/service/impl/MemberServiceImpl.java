package com.atguigu.gmall.ums.service.impl;

import com.atguigu.core.exception.MemberException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.Accessors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String ALPHANUMERIC_UNDERLINE = "[0-9a-zA-Z_]*";

    private final String ums_verification_code = "ums:verificationCode:";

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean checkData(String data, String type) {
        QueryWrapper<MemberEntity> memberEntityQueryWrapper = new QueryWrapper<>();
        switch (type) {
            case "1":
                memberEntityQueryWrapper.eq("username",data);
                break;
            case "2":
                memberEntityQueryWrapper.eq("mobile",data);
                break;
            case "3":
                memberEntityQueryWrapper.eq("email",data);
                break;
            default:
                return false;
        }
        return this.count(memberEntityQueryWrapper) == 0;
    }



    @Override
    public void registerMember(MemberEntity memberEntity, String code) {
        String cacheCode = redisTemplate.opsForValue().get(ums_verification_code + memberEntity.getUsername());
        if (StringUtils.isNotEmpty(cacheCode)
                && cacheCode.equals(code)
                && this.verificationData(memberEntity.getUsername())
                && this.verificationData(memberEntity.getPassword())) {
            memberEntity.setCreateTime(new Date());
            // 实际上来说，密码这块都应该用security，使用String也是不安全的，使用dump内存快照可以把密码看的一清二楚()
            memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword() + "jiayan.123" ));
            memberEntity.setSalt("jiayan.123");
            memberEntity.setSourceType(1);
            memberEntity.setStatus(1);
            memberEntity.setLevelId(1L);
            memberEntity.setIntegration(0);
            memberEntity.setGrowth(0);
            this.save(memberEntity);
            redisTemplate.opsForValue().decrement(ums_verification_code + memberEntity.getUsername());
        }
    }

    @Override
    public MemberEntity queryInfo(String username, String password) {
        if (!this.verificationData(username)
                || !this.verificationData(password)) {
            throw new MemberException("用户名或密码错误");
        }
        String passwordMD5 = DigestUtils.md5Hex(password + "jiayan.123");
        QueryWrapper<MemberEntity> memberEntityQueryWrapper;
        memberEntityQueryWrapper = new QueryWrapper<>();
        memberEntityQueryWrapper.eq("username",username);
        memberEntityQueryWrapper.eq("password",passwordMD5);
        List<MemberEntity> list = this.list(memberEntityQueryWrapper);
        if (list.size() > 1)
            throw new MemberException("不应该有两个用户");
        if (list.size() == 0)
            throw new MemberException("用户名或密码错误");
        MemberEntity memberEntity = list.get(0);
        memberEntity.setPassword(null);
        return memberEntity;
    }

    private boolean verificationData(String str) {
        return str != null && str.matches(ALPHANUMERIC_UNDERLINE);
    }
}