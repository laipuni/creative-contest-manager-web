package com.example.cpsplatform.auth.service;

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

    @Transactional
    public void updateMyInformation(final UpdateMyProfileDto dto) {
        log.info("유저({})가 자신의 프로필을 업데이트합니다.",dto.getLoginId());
        //인증 코드 확인
        Member member = memberService.findMemberByLoginId(dto.getLoginId());

        //이메일이 바뀌었을 경우
        if(member.isChangedEmail(dto.getEmail())){
            log.info("유저({})가 자신의 이메일을 업데이트합니다.",dto.getLoginId());
            //만약 인증하지 않은 경우, AuthCodeMismatchException 예외가 발생함
            authService.verifyAuthCode(dto.getEmail(),dto.getEmail(),"profile_update_verify");
        }

        //if)추후 핸드폰 번호가 바뀌었을 경우, 핸드폰 검증이 필요한 경우도 추가
        //유저 정보 업데이트
        memberService.update(dto.toMemberUpdateDto(member.getId()));
        log.info("유저({})가 자신의 프로필을 업데이트합니다.",dto.getLoginId());
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
