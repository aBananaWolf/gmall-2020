package com.atguigu.com.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author wyl
 * @create 2020-07-04 19:52
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableSwagger2
public class GmallCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmallCartApplication.class,args);
    }
}
