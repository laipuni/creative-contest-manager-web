package com.example.cpsplatform.ai.admin.service;

import com.example.cpsplatform.ai.admin.controller.request.QuestionGenerateRequest;
import com.example.cpsplatform.ai.admin.controller.response.QuestionGenerateResponse;
import com.example.cpsplatform.ai.admin.service.dto.QuestionDto;
import com.example.cpsplatform.exception.AiServerException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
public class AiAdminService{

    private final WebClient webClient;

    public AiAdminService(final WebClient webClient) {
        this.webClient = webClient;
    }

    public List<QuestionDto> generateQuestions(QuestionGenerateRequest request){
        String url = "/generate";
        try {
            QuestionGenerateResponse response = webClient.post()
                    .uri(url)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(QuestionGenerateResponse.class)
                    .block();
            return response.getResponse().stream()
                    .map(this::responseMakeToDto)
                    .toList();
        }catch (WebClientResponseException.BadRequest e){
            throw  new AiServerException("잘못된 요청입니다.", 400, e);
        }catch (WebClientResponseException e) {
            throw new AiServerException("AI 서비스 호출에 실패하였습니다.", e.getRawStatusCode(), e);
        }catch (Exception e) {
            throw new AiServerException("AI 서비스 처리 중 오류가 발생했습니다.", 500, e);
        }
    }

    private QuestionDto responseMakeToDto(String line){
        String[] parts = line.split("\n", 2);
        String question = parts[0].trim();
        String answer = parts[1].trim();
        return new QuestionDto(question, answer);
    }
}
