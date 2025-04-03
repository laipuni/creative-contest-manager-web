package com.example.cpsplatform.auth.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.auth.AuthService;
import com.example.cpsplatform.auth.controller.request.*;
import com.example.cpsplatform.auth.controller.response.FindIdResponse;
import com.example.cpsplatform.member.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegisterService registerService;
    @PostMapping("/api/v1/send-auth-code")
    public ApiResponse<Object> sendAuthCode(@Valid @RequestBody AuthCodeSendRequest request){
        authService.sendAuthCode(request.getRecipient(), request.getSenderType(), request.getStrategyType());
        return ApiResponse.ok(null);
    }

    @PostMapping("/api/v1/find-id")
    public ApiResponse<FindIdResponse> findLoginId(@Valid @RequestBody FindIdRequest request){
        FindIdResponse response = registerService.findId(request.toFindIdDto());
        return ApiResponse.ok(response);
    }

    @PostMapping("/api/password-reset/request")
    public ApiResponse<FindIdResponse> requestPasswordAuthCode(@Valid @RequestBody PasswordSendRequest request){
        registerService.sendPasswordResetAuthCode(request.toPasswordResetCodeDto());
        return ApiResponse.ok(null);
    }
}
