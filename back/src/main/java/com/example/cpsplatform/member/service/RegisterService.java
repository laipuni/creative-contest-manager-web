package com.example.cpsplatform.member.service;

import com.example.cpsplatform.auth.AuthService;
import com.example.cpsplatform.auth.controller.response.FindIdResponse;
import com.example.cpsplatform.exception.PasswordMismatchException;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.service.dto.FindIdDto;
import com.example.cpsplatform.member.service.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.cpsplatform.auth.config.AuthConfig.REGISTER_AUTH;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final MemberService memberService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequestDto request){
        if(request.isPasswordsMatch()){
            //비밀번호 확인과 비밀번호가 다를 경우
            throw new PasswordMismatchException();
        }

        boolean result = authService.verifyAuthCode(
                request.getEmail(), request.getConfirmAuthCode(), REGISTER_AUTH);
        if(result){ //인증 코드가 적절한 경우
            //비밀번호 암호화
            request.encodingPassword(passwordEncoder.encode(request.getPassword()));
            memberService.save(request.toMemberSaveDto());
        }
    }

    public FindIdResponse findId(FindIdDto findIdDto) {
        boolean result = authService.verifyAuthCode(findIdDto.getRecipient(), findIdDto.getAuthCode(), "findId");
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
