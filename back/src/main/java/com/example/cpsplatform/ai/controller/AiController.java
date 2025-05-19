package com.example.cpsplatform.ai.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.ai.admin.controller.request.QuestionGenerateRequest;
import com.example.cpsplatform.ai.admin.service.dto.QuestionDto;
import com.example.cpsplatform.ai.controller.request.FaqRequest;
import com.example.cpsplatform.ai.controller.response.FaqResponse;
import com.example.cpsplatform.ai.service.AiApiService;
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

    @PostMapping("/qa")
    public ApiResponse<FaqResponse> generateFaq(@RequestBody FaqRequest request) {
        FaqResponse result = aiApiService.getAnswerFromFaqChatBot(request);
        return ApiResponse.ok(result);
    }
}
