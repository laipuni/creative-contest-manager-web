package com.example.cpsplatform.ai.service;

import org.springframework.web.reactive.function.client.WebClient;

public class WebClientAiApiService implements AiApiService{

    private final WebClient webClient;

    public WebClientAiApiService(final WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String getTest(String request) {
        return webClient.get()
                .uri(
                        uriBuilder -> uriBuilder
                        .path("/api/test?request={request}")
                        .queryParam("request", request)
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public TestResponse postTest(TestRequest request) {
        String url = "/api/test";
        return webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(TestResponse.class)
                .blockFirst();

    }
}
