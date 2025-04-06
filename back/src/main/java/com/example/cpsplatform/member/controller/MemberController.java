package com.example.cpsplatform.member.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.member.controller.request.MemberRegisterReqeust;
import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final RegisterService registerService;
    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public ApiResponse<Object> register(@Valid @RequestBody MemberRegisterReqeust reqeust){
        registerService.register(reqeust.toRegisterRequest());
        return ApiResponse.ok(null);
    }

    @GetMapping("/api/check-id")
    public ApiResponse<Boolean> checkLoginId(@RequestParam(value = "loginId",defaultValue = "") String loginId){
        boolean result = memberService.isUsernameExists(loginId);
        return ApiResponse.ok(result);
    }
}
