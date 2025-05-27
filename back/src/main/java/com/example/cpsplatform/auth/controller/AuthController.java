package com.example.cpsplatform.auth.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.auth.config.AuthConfig;
import com.example.cpsplatform.auth.controller.request.ProfileCodeVerifyRequest;
import com.example.cpsplatform.auth.controller.response.PasswordConfirmResponse;
import com.example.cpsplatform.auth.controller.response.ProfilePasswordVerifyResponse;
import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.auth.controller.request.*;
import com.example.cpsplatform.auth.controller.response.FindIdResponse;
import com.example.cpsplatform.auth.service.PasswordResetService;
import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.member.service.ProfileService;
import com.example.cpsplatform.security.domain.SecurityMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegisterService registerService;
    private final PasswordResetService passwordResetService;
    private final ProfileService profileService;

    @PostMapping("/api/v1/send-auth-code")
    public ApiResponse<Object> sendAuthCode(@Valid @RequestBody AuthCodeSendRequest request){
        authService.sendAuthCode(request.getRecipient(), request.getSenderType(), request.getStrategyType());
        return ApiResponse.ok(null);
    }

    @PostMapping("/api/v1/send-register-code")
    public ApiResponse<Object> sendRegisterAuthCode(@Valid @RequestBody RegisterAuthCodeSendRequest request){
        //todo 해당 정보로 가입한 유저가 있는지 확인해야 함
        authService.sendAuthCode(request.getRecipient(), request.getSenderType(), request.getStrategyType());
        return ApiResponse.ok(null);
    }

    @PostMapping("/api/verify-register-code")
    public ApiResponse<Object> verifyRegisterAuthCode(@Valid @RequestBody AuthCodeVerifyRequest request){
        authService.verifyContactCode(request.getRecipient(), request.getAuthCode(), request.getStrategyType(), AuthConfig.SIGNUP_VERIFY_AUTH);
        return ApiResponse.ok(null);
    }

    @PostMapping("/api/v1/find-id")
    public ApiResponse<FindIdResponse> findLoginId(@Valid @RequestBody FindIdRequest request){
        FindIdResponse response = registerService.findId(request.toFindIdDto());
        return ApiResponse.ok(response);
    }

    @PostMapping("/api/password-reset/request")
    public ApiResponse<Object> requestPasswordAuthCode(@Valid @RequestBody PasswordSendRequest request){
        passwordResetService.sendPasswordResetAuthCode(request.toPasswordResetCodeDto());
        return ApiResponse.ok(null);
    }

    @PostMapping("/api/password-reset/confirm")
    public ApiResponse<PasswordConfirmResponse> confirmPasswordAuthCode(@Valid @RequestBody PasswordConfirmRequest request){
        PasswordConfirmResponse response = passwordResetService.confirmPasswordAuthCode(request.toPasswordConfirmDto());
        return ApiResponse.ok(response);
    }

    @PostMapping("/api/password-reset")
    public ApiResponse<Object> resetPassword(@Valid @RequestBody PasswordResetRequest request){
        passwordResetService.resetPassword(request.toResetPasswordDto());
        return ApiResponse.ok(null);
    }


}
