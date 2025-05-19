package com.example.cpsplatform.ai.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionGenerateRequest {
    private String topic;
    private String type;
    private int count;
}
