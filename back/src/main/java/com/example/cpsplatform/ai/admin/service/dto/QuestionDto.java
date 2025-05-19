package com.example.cpsplatform.ai.service.dto;

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
