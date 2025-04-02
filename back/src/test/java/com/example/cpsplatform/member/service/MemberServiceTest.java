package com.example.cpsplatform.member.service;

import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.dto.MemberSaveDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @DisplayName("저장할 회원의 정보를 받아서 회원을 저장한다.")
    @Test
    void save(){
        //given
        String loginId = "loginId";
        String password = "password";
        String encodingPassword = passwordEncoder.encode(password);
        String name = "name";
        LocalDate birth = LocalDate.of(2000,1,1);
        Gender gender = Gender.MAN;
        Address address = new Address("street","zipCode","detail");
        String phoneNumber = "010xxxxXXXX";
        String email = "email@email.com";
        School school = new School("xx초등학교", StudentType.ELEMENTARY,1);

        MemberSaveDto saveDto = new MemberSaveDto(loginId,encodingPassword,name,
                birth,gender,address,phoneNumber,email,school);


        //when
        memberService.save(saveDto);
        List<Member> result = memberRepository.findAll();
        //then
        assertThat(result.get(0)).isNotNull()
                .extracting("loginId","password","name",
                        "birth","gender","address","phoneNumber","email","organization")
                .containsExactly(loginId,encodingPassword,name,
                        birth,gender,address,phoneNumber,email,school);
    }


}