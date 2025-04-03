package com.example.cpsplatform.member.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetCodeDto {

    private String loginId;
    private String recipient;
    private String senderType;

}
