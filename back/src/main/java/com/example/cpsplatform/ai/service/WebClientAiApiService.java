package com.example.cpsplatform.ai.service;

import com.example.cpsplatform.ai.controller.request.FaqRequest;
import com.example.cpsplatform.ai.controller.response.FaqResponse;
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

    @Override
    public FaqResponse getAnswerFromFaqChatBot(FaqRequest request){
        String url = "/qa";
        return webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FaqResponse.class)
                .block();
    }
}
