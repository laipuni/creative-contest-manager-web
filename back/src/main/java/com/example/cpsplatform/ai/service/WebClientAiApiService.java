package com.example.cpsplatform.ai.service;

import com.example.cpsplatform.ai.controller.request.QuestionGenerateRequest;
import com.example.cpsplatform.ai.controller.response.QuestionGenerateResponse;
import com.example.cpsplatform.ai.service.dto.QuestionDto;
import java.util.List;
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
    public List<QuestionDto> generateQuestions(QuestionGenerateRequest request){
        String url = "/generate";
        QuestionGenerateResponse response = webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(QuestionGenerateResponse.class)
                .block();
        return response.getResponse().stream()
                .map(this::responseMakeToDto)
                .toList();
    }

    private QuestionDto responseMakeToDto(String line){
        String[] parts = line.split("\n", 2);
        String question = parts[0].trim();
        String answer = parts[1].trim();
        return new QuestionDto(question, answer);
    }
}
