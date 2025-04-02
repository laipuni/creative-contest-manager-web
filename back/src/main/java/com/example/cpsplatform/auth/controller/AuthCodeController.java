package com.example.cpsplatform.auth.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.auth.AuthService;
import com.example.cpsplatform.auth.controller.request.AuthCodeSendRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthCodeController {

    private final AuthService authService;
    @PostMapping("/api/v1/send-auth-code")
    public ApiResponse<Object> sendAuthCode(@Valid @RequestBody AuthCodeSendRequest request){
        authService.sendAuthCode(request.getRecipient(), request.getSenderType(), request.getStrategyType());
        return ApiResponse.ok(null);
    }



}
