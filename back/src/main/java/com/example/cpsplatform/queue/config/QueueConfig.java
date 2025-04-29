package com.example.cpsplatform.queue.config;

import com.example.cpsplatform.queue.service.QueueService;
import com.example.cpsplatform.queue.service.RedissonQueueService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Autowired
    RedissonClient redissonClient;

    @Bean
    public QueueService queueService(){
        return new RedissonQueueService(redissonClient);
    }

}
