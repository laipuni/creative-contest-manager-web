package com.example.cpsplatform.ai.admin.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionGenerateRequest {
    private String topic;
    private String level;
    private int count;
}
