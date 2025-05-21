package com.example.cpsplatform.ai.service;

import com.example.cpsplatform.ai.controller.request.FaqRequest;
import com.example.cpsplatform.ai.controller.response.FaqResponse;
import com.example.cpsplatform.exception.AiServerException;
import com.example.cpsplatform.exception.ClientRequestException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
        try {
            return webClient.post()
                    .uri(url)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(FaqResponse.class)
                    .block();
        }catch (WebClientResponseException.BadRequest e){
            throw new ClientRequestException("잘못된 요청입니다.");
        }catch (WebClientResponseException e) {
            throw new AiServerException("AI 서비스 호출에 실패하였습니다.");
        }catch (Exception e) {
            throw new AiServerException("AI 서비스 시스템 처리 중 오류가 발생했습니다.");
        }
    }
}
