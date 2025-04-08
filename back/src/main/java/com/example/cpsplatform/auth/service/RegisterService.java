package com.example.cpsplatform.auth.service;

import com.example.cpsplatform.auth.controller.response.FindIdResponse;
import com.example.cpsplatform.exception.DuplicateDataException;
import com.example.cpsplatform.exception.PasswordMismatchException;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.service.MemberService;
import com.example.cpsplatform.auth.service.dto.FindIdDto;
import com.example.cpsplatform.auth.service.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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
                request.getEmail(), request.getEmail(), "signup_verify");

        if(result){
            //인증 코드가 적절한 경우
            try {
                //todo 회원 중복 예외 다른방식으로 처리 요함
                memberService.save(request.toMemberSaveDto());
            } catch (DataIntegrityViolationException e){
                throw new DuplicateDataException("중복된 회원이 존재합니다.");
            }
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
