package com.example.cpsplatform.security.config;

import com.example.cpsplatform.redis.RedisRepository;
import com.example.cpsplatform.security.service.LoginFailService;
import com.example.cpsplatform.security.service.RedisLoginFailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class LoginConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LoginFailService loginFailService(RedisRepository redisRepository){
        return new RedisLoginFailService(redisRepository);
    }

}
