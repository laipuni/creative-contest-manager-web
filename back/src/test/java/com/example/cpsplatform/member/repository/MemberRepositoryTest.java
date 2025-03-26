package com.example.cpsplatform.member.repository;

import com.example.cpsplatform.member.Member;
import com.example.cpsplatform.member.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("해당 아이디의 유저를 단건 조회한다.")
    @Test
    void findMemberByLoginId(){
        //given
        String loginId = "loginId";
        Member member = Member.builder()
                .loginId(loginId)
                .password("password")
                .role(Role.USER)
                .build();
        memberRepository.save(member);
        //when
        Member result = memberRepository.findMemberByLoginId(loginId).get();

        //then
        assertThat(result).isNotNull()
                .extracting("loginId","password","role")
                .containsExactly(member.getLoginId(),member.getPassword(),member.getRole());
    }

}