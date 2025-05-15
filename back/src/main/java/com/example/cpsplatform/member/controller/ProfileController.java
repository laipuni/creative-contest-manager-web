package com.example.cpsplatform.member.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.auth.controller.request.ProfileCodeSendRequest;
import com.example.cpsplatform.auth.controller.request.ProfileCodeVerifyRequest;
import com.example.cpsplatform.auth.controller.request.ProfilePasswordVerifyRequest;
import com.example.cpsplatform.auth.controller.response.ProfilePasswordVerifyResponse;
import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.member.controller.request.MyProfileUpdateRequest;
import com.example.cpsplatform.member.controller.response.MyProfileResponse;
import com.example.cpsplatform.member.controller.response.UserInfoResponse;
import com.example.cpsplatform.member.service.ProfileService;
import com.example.cpsplatform.security.domain.SecurityMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final AuthService authService;


    /**
     * 이메일, 전화번호 같이 인증 정보가 바뀌었을 경우 인증 코드 전송 api
     * @param request 이메일, 전화번호 같이 인증 정보, 전송 타입, 인증 전략
     * @return 정상적으로 전송했을 경우, 200반환
     */
    @PostMapping("/api/members/profile/send-update-code")
    public ApiResponse<Object> sendProfileUpdateCode(@Valid @RequestBody ProfileCodeSendRequest request){
        authService.sendAuthCode(request.getRecipient(), request.getSenderType(), request.getStrategyType());
        return ApiResponse.ok(null);
    }

    /**
     * 이메일, 전화번호 같이 인증 정보가 바뀌었을 경우 인증 코드 검증 api
     * @param request 이메일, 전화번호 같이 인증 정보, 인증 코드, 인증 전략
     * @return 정상적으로 검증했을 경우, 200반환
     */
    @PostMapping("/api/members/profile/verify-update-code")
    public ApiResponse<Object> sendProfileUpdateCode(@Valid @RequestBody ProfileCodeVerifyRequest request){
        authService.verifyContactCode(request.getRecipient(), request.getAuthCode(), request.getStrategyType(),"profile_update_verify");
        return ApiResponse.ok(null);
    }

    /**
     * 프로필 조회를 위한 비밀번호 검증 api
     * @param request 이메일, 전화번호 같이 인증 정보, 인증 코드, 인증 전략
     * @return 정상적으로 검증했을 경우, 프로필 조회 세션 값 반환
     */
    @PostMapping("/api/members/profile/password-verify")
    public ApiResponse<ProfilePasswordVerifyResponse> verifyProfilePassword(@Valid @RequestBody ProfilePasswordVerifyRequest request,
                                                                            @AuthenticationPrincipal SecurityMember member){
        ProfilePasswordVerifyResponse response = profileService.verifyProfileUpdatePassword(request.getPassword(), member.getUsername());
        return ApiResponse.ok(response);
    }

    /**
     * 자신의 프로필 조회 api
     * 세션 값은 프로필 비밀번호 인증 api에서 얻은 session값을 말한다.
     * @param session 프로필 조회를 위한 세션 값, 없을 경우 프로필을 조회할 수 없다.
     * @param securityMember 로그인 정보가 들어있는 파라미터
     * @return 자신의 프로필 정보를 응답한다.
     */
    @GetMapping("/api/members/my-profile")
    public ApiResponse<MyProfileResponse> getMyInfo(@RequestParam(value = "session",defaultValue = "") String session,
                                                    @AuthenticationPrincipal SecurityMember securityMember){
        MyProfileResponse myProfileResponse = profileService.getMyInformation(securityMember.getUsername(),session);
        return ApiResponse.ok(myProfileResponse);
    }

    /**
     * 자신의 프로필을 수정하는 api
     * 프로필 조회와 똑같이 세션값이 필요하다
     * @param request 수정할 프로필의 정보를 받아온다.
     * @param securityMember 로그인 정보가 들어있는 파라미터
     * @return 성공적으로 수정에 성공했을 경우 ok를 반환
     */
    @PatchMapping("/api/members/my-profile")
    public ApiResponse<Object> updateMyInfo(@Valid @RequestBody MyProfileUpdateRequest request,
                                            @AuthenticationPrincipal SecurityMember securityMember){
        profileService.updateMyInformation(request.toUpdateMyProfileDto(securityMember.getUsername()));
        return ApiResponse.ok(null);
    }

    /**
     * 유저의 프로필 정보를 받는 api
     * @param securityMember 로그인 정보가 들어있는 파라미터
     * @return 이름, 아이디 값을 반환한다.
     */
    @GetMapping("/api/members/user-info")
    public ApiResponse<UserInfoResponse> getUserInfo(@AuthenticationPrincipal SecurityMember securityMember){
        return ApiResponse.ok(UserInfoResponse.of(securityMember.getUsername(), securityMember.getName()));
    }
}
