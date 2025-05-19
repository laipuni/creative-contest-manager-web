package com.example.cpsplatform.ai.admin.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QuestionDto {
    private String question;
    private String answer;
}
