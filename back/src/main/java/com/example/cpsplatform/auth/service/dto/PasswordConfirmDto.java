package com.example.cpsplatform.auth.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordConfirmDto {

    private String loginId;
    private String recipient;
    private String senderType;
    private String authCode;

}
