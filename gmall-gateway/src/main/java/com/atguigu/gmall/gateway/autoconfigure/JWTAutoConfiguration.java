package com.atguigu.gmall.gateway.autoconfigure;

import com.atguigu.gmall.gateway.properties.JWTProperties;
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
