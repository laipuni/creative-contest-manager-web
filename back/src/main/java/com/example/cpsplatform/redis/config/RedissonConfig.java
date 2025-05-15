package com.example.cpsplatform.redis.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.useSsl}")
    private boolean useSsl;

    @Bean
    public RedissonClient redissonClient() {
        String protocol = useSsl ? "rediss" : "redis";
        Config config = new Config();
        //Redis 서버 연결 설정
        config.useSingleServer()
                .setAddress(String.format("%s://%s:%d",protocol,redisHost,redisPort));
        //직렬화 Jackson JSON 방식 설정
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }
}
