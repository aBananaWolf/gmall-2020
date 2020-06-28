package com.atguigu.gmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author wyl
 * @create 2020-06-11 13:28
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GmallGateWayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallGateWayApplication.class, args);
    }
}
