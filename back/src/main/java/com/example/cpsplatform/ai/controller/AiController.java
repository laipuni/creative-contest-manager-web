package com.example.cpsplatform.ai.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.ai.controller.request.QuestionGenerateRequest;
import com.example.cpsplatform.ai.service.AiApiService;
import com.example.cpsplatform.ai.service.dto.QuestionDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiApiService aiApiService;

    @PostMapping("/generate")
    public ApiResponse<List<QuestionDto>> generateQuestions(@RequestBody QuestionGenerateRequest request) {
        List<QuestionDto> result = aiApiService.generateQuestions(request);
        return ApiResponse.ok(result);
    }
}
