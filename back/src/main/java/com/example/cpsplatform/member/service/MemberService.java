package com.example.cpsplatform.member.service;

import com.example.cpsplatform.member.controller.response.MyProfileResponse;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.dto.MemberSaveDto;
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
        return memberRepository.findMemberByEmailAndRole(email, Role.USER)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));
    }

    public Member findMemberByEmailAndLoginId(final String email, final String loginId) {
        log.debug("이메일({})과 아이디({})에 해당하는 유저 조회 시도", email, loginId);
        return memberRepository.findMemberByEmailAndLoginIdAndRole(email, loginId, Role.USER)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));
    }

    public Member findMemberByLoginId(final String loginId){
        log.debug("아이디({})에 해당하는 유저 조회 시도", loginId);
        return memberRepository.findMemberByLoginIdAndRole(loginId,Role.USER)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));
    }

    public MyProfileResponse getMyInformation(final String loginId){
        log.debug("유저({})의 프로필 조회 시도", loginId);
        Member member = memberRepository.findMemberByLoginId(loginId)
                .orElseThrow(()->new IllegalArgumentException("프로필 정보를 불러오는데 실패했습니다."));
        return MyProfileResponse.of(member);
    }

    public boolean isUsernameExists(String username) {
        return memberRepository.existsByLoginId(username);
    }
}
