package com.example.cpsplatform.security.service;

import com.example.cpsplatform.exception.LoginFailedException;
import com.example.cpsplatform.member.Member;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.domain.SecurityMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByLoginId(username)
                .orElseThrow(() -> new LoginFailedException("아이디 혹은 비밀번호가 일치하지 않습니다."));

        return new SecurityMember(member);
    }
}
