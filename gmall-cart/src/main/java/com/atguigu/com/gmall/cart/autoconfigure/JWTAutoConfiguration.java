package com.atguigu.com.gmall.cart.autoconfigure;

import com.atguigu.com.gmall.cart.properties.JWTProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wyl
 * @create 2020-07-03 12:14
 */
@Configuration
@EnableConfigurationProperties(JWTProperties.class)
public class JWTAutoConfiguration {
}
