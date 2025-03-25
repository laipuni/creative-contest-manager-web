package com.example.cpsplatform.ai.config;

import com.example.cpsplatform.ai.service.AiApiService;
import com.example.cpsplatform.ai.service.WebClientAiApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiConfig {

    @Bean
    public AiApiService aiApiService(){
        return new WebClientAiApiService(webClient());
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public WebClient webClient() {
        return WebClient.builder()
                //todo 도커를 사용할 때는 services이름으로 변경해야 함
                .baseUrl("http://localhost:5000")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
