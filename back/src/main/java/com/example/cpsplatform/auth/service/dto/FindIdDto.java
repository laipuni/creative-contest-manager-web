package com.example.cpsplatform.auth.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindIdDto {

    private String recipient;
    private String authCode;
    private String senderType;

}
