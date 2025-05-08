package com.example.cpsplatform.member.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.member.controller.request.MemberRegisterRequest;
import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.member.controller.response.MyProfileResponse;
import com.example.cpsplatform.member.service.MemberService;
import com.example.cpsplatform.security.domain.SecurityMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final RegisterService registerService;
    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public ApiResponse<Object> register(@Valid @RequestBody MemberRegisterRequest reqeust){
        registerService.register(reqeust.toRegisterRequest());
        return ApiResponse.ok(null);
    }

    @GetMapping("/api/check-id")
    public ApiResponse<Boolean> checkLoginId(@RequestParam(value = "loginId",defaultValue = "") String loginId){
        boolean result = memberService.isUsernameExists(loginId);
        return ApiResponse.ok(result);
    }

    @GetMapping("/api/me")
    public ApiResponse<MyProfileResponse> getMyInfo(@AuthenticationPrincipal SecurityMember securityMember){
        MyProfileResponse myProfileResponse = memberService.getMyInformation(securityMember.getUsername());
        return ApiResponse.ok(myProfileResponse);
    }
}
