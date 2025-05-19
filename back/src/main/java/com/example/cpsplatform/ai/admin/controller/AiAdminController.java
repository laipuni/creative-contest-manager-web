package com.example.cpsplatform.ai.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.ai.admin.controller.request.QuestionGenerateRequest;
import com.example.cpsplatform.ai.admin.service.AiAdminService;
import com.example.cpsplatform.ai.admin.service.dto.QuestionDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/ai")
@RequiredArgsConstructor
public class AiAdminController {

    private final AiAdminService aiAdminService;

    @AdminLog
    @PostMapping("/generate")
    public ApiResponse<List<QuestionDto>> generateQuestions(@RequestBody QuestionGenerateRequest request) {
        List<QuestionDto> result = aiAdminService.generateQuestions(request);
        return ApiResponse.ok(result);
    }

}
