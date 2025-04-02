package com.example.cpsplatform.member.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.member.controller.request.MemberRegisterReqeust;
import com.example.cpsplatform.member.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final RegisterService registerService;

    @PostMapping("/api/v1/members")
    public ApiResponse<Object> register(@Valid @RequestBody MemberRegisterReqeust reqeust){
        registerService.register(reqeust.toRegisterRequest());
        return ApiResponse.ok(null);
    }

}
