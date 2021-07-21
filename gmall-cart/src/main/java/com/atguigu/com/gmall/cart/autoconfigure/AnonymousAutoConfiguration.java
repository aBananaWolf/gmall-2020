package com.atguigu.com.gmall.cart.autoconfigure;

import com.atguigu.com.gmall.cart.properties.AnonymousProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wyl
 * @create 2020-07-04 20:29
 */

@Configuration
@EnableConfigurationProperties(AnonymousProperties.class)
public class AnonymousAutoConfiguration {
}
