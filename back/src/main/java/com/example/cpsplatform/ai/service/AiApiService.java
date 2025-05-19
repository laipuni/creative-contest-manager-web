package com.example.cpsplatform.ai.service;


import com.example.cpsplatform.ai.controller.request.QuestionGenerateRequest;
import com.example.cpsplatform.ai.service.dto.QuestionDto;
import java.util.List;

public interface AiApiService {

    String getTest(String reqeust);
    TestResponse postTest(TestRequest request);

    List<QuestionDto> generateQuestions(QuestionGenerateRequest request);
}
