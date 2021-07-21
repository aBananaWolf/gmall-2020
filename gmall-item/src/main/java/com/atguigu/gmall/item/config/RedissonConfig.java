package com.atguigu.gmall.item.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wyl
 * @create 2020-06-27 12:52
 */
@Configuration
public class RedissonConfig {
    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient RedissonClient() {
        Config config = new Config();
        if (redisProperties.getCluster()!= null) {
            redisProperties.getCluster().getNodes().forEach(
                    redisNode -> {
                        config.useClusterServers().addNodeAddress(redisNode);
                    }
            );
        } else {
            if (redisProperties.getHost() != null)
                config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
            else return null;
        }
        return Redisson.create(config);
    }
}
