package com.atguigu.com.gmall.cart.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * @author wyl
 * @create 2020-07-04 20:38
 */
@ConfigurationProperties("com.atguigu.anonymous")
public class AnonymousProperties {
    private String userKeyName;
    private int expire;

    @PostConstruct
    void init() {
        expire = expire * 60;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String getUserKeyName() {
        return userKeyName;
    }

    public void setUserKeyName(String userKeyName) {
        this.userKeyName = userKeyName;
    }
}
