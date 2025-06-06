package com.example.cpsplatform.member.service;

import com.example.cpsplatform.member.controller.response.MyProfileResponse;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.dto.MemberSaveDto;
import com.example.cpsplatform.member.service.dto.MemberUpdateDto;
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
public class MemberService {

    private final MemberRepository memberRepository;
    private final CryptoService cryptoService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void save(final MemberSaveDto saveDto){
        memberRepository.save(saveDto.toEntity(cryptoService,passwordEncoder));
        log.info("{} 유저 회원가입",saveDto.getLoginId());
    }

    public Member findMemberByEmail(final String email) {
        log.debug("이메일이 {}인 유저 조회 시도", email);
        String encodedEmail = getEncodedEmail(email);
        return memberRepository.findMemberByEmailAndRole(encodedEmail, Role.USER)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));
    }

    private String getEncodedEmail(final String email) {
        return cryptoService.encryptAES(email);
    }

    public Member findMemberByEmailAndLoginId(final String email, final String loginId) {
        log.debug("이메일({})과 아이디({})에 해당하는 유저 조회 시도", email, loginId);
        String encodedEmail = getEncodedEmail(email);
        return memberRepository.findMemberByEmailAndLoginIdAndRole(encodedEmail, loginId, Role.USER)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));
    }

    public Member findMemberByLoginId(final String loginId){
        log.debug("아이디({})에 해당하는 유저 조회 시도", loginId);
        return memberRepository.findMemberByLoginIdAndRole(loginId,Role.USER)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));
    }

    public boolean isUsernameExists(String username) {
        return memberRepository.existsByLoginId(username);
    }

    @Transactional
    public void update(final MemberUpdateDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 수정하는데 실패했습니다."));
        member.update(dto, cryptoService);
    }
}
