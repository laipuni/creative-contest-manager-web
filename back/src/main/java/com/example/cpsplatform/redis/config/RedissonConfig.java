package com.example.cpsplatform.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        //Redis 서버 연결 설정
        config.useSingleServer()
                .setAddress("redis://localhost:6379");
        //직렬화 Jackson JSON 방식 설정
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }
}
