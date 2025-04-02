package com.example.cpsplatform.member.repository;

import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("해당 아이디의 유저를 단건 조회한다.")
    @Test
    void findMemberByLoginId(){
        //given
        String loginId = "loginId";
        Address address = new Address("street","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("010xxxxxxxx")
                .name("사람 이름")
                .organization(school)
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