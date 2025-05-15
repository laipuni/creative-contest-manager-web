package com.example.cpsplatform.auth.service;

import com.example.cpsplatform.auth.controller.response.PasswordConfirmResponse;
import com.example.cpsplatform.auth.service.dto.PasswordConfirmDto;
import com.example.cpsplatform.auth.service.dto.PasswordResetDto;
import com.example.cpsplatform.auth.service.session.SessionType;
import com.example.cpsplatform.exception.PasswordMismatchException;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.service.MemberService;
import com.example.cpsplatform.auth.service.dto.PasswordResetCodeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.cpsplatform.auth.service.session.SessionType.PASSWORD_RESET;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final MemberService memberService;
    private final AuthService authService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    public void sendPasswordResetAuthCode(PasswordResetCodeDto resetCodeDto){
        Member member = findMember(resetCodeDto);
        if(member != null){
            //아이디와 이메일에 해당하는 유저가 존재할 경우
            authService.sendAuthCode(resetCodeDto.getRecipient(),
                    resetCodeDto.getSenderType(),"password_auth");
        }
    }

    private Member findMember(final PasswordResetCodeDto resetCodeDto) {
        return switch (resetCodeDto.getSenderType()){
            case "email" -> memberService.findMemberByEmailAndLoginId(
                    resetCodeDto.getRecipient(), resetCodeDto.getLoginId());
            default -> null;
        };
    }

    public PasswordConfirmResponse confirmPasswordAuthCode(PasswordConfirmDto confirmDto){
        authService.verifyAuthCode(confirmDto.getRecipient(), confirmDto.getAuthCode(), "password_auth");
        String session = sessionService.storeSession(confirmDto.getLoginId(), PASSWORD_RESET);
        return new PasswordConfirmResponse(session);
    }

    @Transactional
    public void resetPassword(PasswordResetDto passwordDto){
        //비밀번호 재설정 세션이 유효한지 체크
        sessionService.confirmSession(
                passwordDto.getLoginId(),passwordDto.getSession(), PASSWORD_RESET
        );

        //재설정할 비밀번호와 비밀번호 확인이 같은지 확인
        if(passwordDto.isMistMatchPassword()){
            throw new PasswordMismatchException();
        }
        //비밀번호 재설정
        String encodedNewPassword = passwordEncoder.encode(passwordDto.getResetPassword());
        Member member = memberService.findMemberByLoginId(passwordDto.getLoginId());
        member.changePassword(encodedNewPassword);
    }

}
