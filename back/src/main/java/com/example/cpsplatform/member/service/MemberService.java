package com.example.cpsplatform.member.service;

import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.dto.MemberSaveDto;
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

    @Transactional
    public void save(final MemberSaveDto saveDto){
        memberRepository.save(saveDto.toEntity());
        log.info("{} 유저 회원가입",saveDto.getLoginId());
    }

    public Member findMemberByEmail(final String email) {
        return memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> {
                    log.debug("이메일이 {}인 유저 조회 실패",email);
                    return new IllegalArgumentException("해당 유저는 존재하지 않습니다.");
                });
    }
}
