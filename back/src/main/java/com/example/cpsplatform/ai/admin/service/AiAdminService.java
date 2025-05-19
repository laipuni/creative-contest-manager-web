package com.example.cpsplatform.ai.admin.service;

import com.example.cpsplatform.ai.admin.controller.request.QuestionGenerateRequest;
import com.example.cpsplatform.ai.admin.controller.response.QuestionGenerateResponse;
import com.example.cpsplatform.ai.admin.service.dto.QuestionDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class AiAdminService{

    private final WebClient webClient;

    public AiAdminService(final WebClient webClient) {
        this.webClient = webClient;
    }

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
