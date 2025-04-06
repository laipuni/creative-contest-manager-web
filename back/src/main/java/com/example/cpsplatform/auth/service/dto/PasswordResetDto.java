package com.example.cpsplatform.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetDto {

    private String session;

    private String loginId;

    private String resetPassword;

    private String confirmPassword;

    public boolean isMistMatchPassword(){
        return !resetPassword.equals(confirmPassword);
    }

}
