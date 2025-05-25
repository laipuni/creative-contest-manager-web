package com.example.cpsplatform.ai.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FaqRequest {

    @NotBlank(message = "질문은 필수입니다.")
    private String question;
}
