package com.example.cpsplatform.member.service;

import com.example.cpsplatform.auth.config.AuthConfig;
import com.example.cpsplatform.auth.controller.response.ProfilePasswordVerifyResponse;
import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.auth.service.SessionService;
import com.example.cpsplatform.auth.service.session.SessionType;
import com.example.cpsplatform.exception.PasswordMismatchException;
import com.example.cpsplatform.member.controller.response.MyProfileResponse;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.service.dto.UpdateMyProfileDto;
import com.example.cpsplatform.security.encoder.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {

    private final MemberService memberService;
    private final AuthService authService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final CryptoService cryptoService;

    /**
     * 프로필을 수정하기 위해서 비밀번호 인증 후 세션을 캐싱함
     * 프로필을 수정할 때, 해당 세션이 있어야 프로필을 수정 가능함
     * @return ProfilePasswordVerifyResponse : 프로필 수정 세션을 반환함
     * @throws PasswordMismatchException : 비밀번호가 일치하지 않을 때 발생
     */
    public ProfilePasswordVerifyResponse verifyProfileUpdatePassword(final String password, final String username){
        Member member = memberService.findMemberByLoginId(username);
        if(!passwordEncoder.matches(password,member.getPassword())){
            //비밀번호가 일치하지 않을 경우
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        String session = sessionService.storeSession(username, SessionType.PROFILE);
        return ProfilePasswordVerifyResponse.of(session);
    }

    @Transactional
    public void updateMyInformation(final UpdateMyProfileDto dto) {
        log.info("유저({})가 프로필을 업데이트합니다.",dto.getLoginId());

        //세션 확인, 유효한 세션이 아닐경우 InvalidSessionException 발생
        sessionService.confirmSession(dto.getLoginId(),dto.getSession(),SessionType.PROFILE);

        Member member = memberService.findMemberByLoginId(dto.getLoginId());
        //이메일이 바뀌었을 경우
        if(member.isChangedEmail(dto.getEmail())){
            log.info("유저({})가 자신의 이메일을 업데이트합니다.",dto.getLoginId());
            //만약 인증하지 않은 경우, AuthCodeMismatchException 예외가 발생함
            authService.verifyAuthCode(dto.getEmail(),dto.getEmail(), AuthConfig.PROFILE_UPDATE_VERIFY_AUTH);
        }

        //if)추후 핸드폰 번호가 바뀌었을 경우, 핸드폰 검증이 필요한 경우도 추가
        //유저 정보 업데이트
        memberService.update(dto.toMemberUpdateDto(member.getId()));
        log.info("유저({})가 자신의 프로필을 업데이트합니다.",dto.getLoginId());
    }

    /**
     * 자신의 프로필을 조회하는 메서드
     * @param loginId 조회할 프로필의 아이디
     * @param session 프로필을 조회할 세션 값
     * @return 프로필 정보
     */
    public MyProfileResponse getMyInformation(final String loginId,final String session){
        log.debug("유저({})의 프로필 조회 시도", loginId);
        //세션 확인, 유효한 세션이 아닐경우 InvalidSessionException 발생
        sessionService.confirmSession(loginId,session,SessionType.PROFILE);
        Member member = memberService.findMemberByLoginId(loginId);
        return MyProfileResponse.of(member,cryptoService);
    }
}
