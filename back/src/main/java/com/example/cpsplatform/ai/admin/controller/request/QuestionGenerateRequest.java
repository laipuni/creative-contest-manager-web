package com.example.cpsplatform.ai.admin.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionGenerateRequest {

    @NotBlank(message = "유형 정보는 필수입니다.")
    private String topic;

    @NotBlank(message = "난이도 정보는 필수입니다.")
    private String level;

    @Min(value = 1, message = "문제 개수는 1 이상이어야 합니다.")
    private int count;
}
