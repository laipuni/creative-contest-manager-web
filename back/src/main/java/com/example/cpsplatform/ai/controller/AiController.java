package com.example.cpsplatform.ai.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.ai.controller.request.FaqRequest;
import com.example.cpsplatform.ai.controller.response.FaqResponse;
import com.example.cpsplatform.ai.service.AiApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final AiApiService aiApiService;

    @PostMapping("/api/questions")
    public ApiResponse<FaqResponse> generateFaq(@RequestBody FaqRequest request) {
        FaqResponse result = aiApiService.getAnswerFromFaqChatBot(request);
        return ApiResponse.ok(result);
    }
}
