package com.example.cpsplatform.auth.service;

import com.example.cpsplatform.auth.config.AuthConfig;
import com.example.cpsplatform.auth.controller.response.FindIdResponse;
import com.example.cpsplatform.exception.DuplicateDataException;
import com.example.cpsplatform.exception.PasswordMismatchException;
import com.example.cpsplatform.member.controller.response.MyProfileResponse;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.service.MemberService;
import com.example.cpsplatform.auth.service.dto.FindIdDto;
import com.example.cpsplatform.auth.service.dto.RegisterRequestDto;
import com.example.cpsplatform.member.service.dto.UpdateMyProfileDto;
import com.example.cpsplatform.security.encoder.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {

    private final MemberService memberService;
    private final AuthService authService;

    public void register(RegisterRequestDto request){
        if(request.isMismatchPasswords()){
            //비밀번호 확인과 비밀번호가 다를 경우
            throw new PasswordMismatchException();
        }

        boolean result = authService.verifyAuthCode(
                request.getEmail(), request.getEmail(), AuthConfig.SIGNUP_VERIFY_AUTH);

        if(result){
            memberService.save(request.toMemberSaveDto());
        }
    }

    public FindIdResponse findId(FindIdDto findIdDto) {
        boolean result = authService.verifyAuthCode(findIdDto.getRecipient(), findIdDto.getAuthCode(), AuthConfig.FIND_ID_AUTH);
        if(!result){
            return null;
        }
        String loginId = findLoginId(findIdDto);
        return FindIdResponse.of(loginId);
    }

    private String findLoginId(final FindIdDto findIdDto) {
        Member member = switch (findIdDto.getSenderType()) {
            case "email" -> memberService.findMemberByEmail(findIdDto.getRecipient());
            default -> null;
        };
        if(member != null){
            return member.getLoginId();
        }
        return null;
    }

}
