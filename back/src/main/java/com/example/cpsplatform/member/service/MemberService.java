package com.example.cpsplatform.member.service;

import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.dto.MemberSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void save(final MemberSaveDto saveDto){
        memberRepository.save(saveDto.toEntity());
    }

}
