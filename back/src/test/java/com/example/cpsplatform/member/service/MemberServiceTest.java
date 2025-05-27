package com.example.cpsplatform.member.service;

import com.example.cpsplatform.member.controller.response.MyProfileResponse;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.dto.MemberSaveDto;
import com.example.cpsplatform.security.encoder.CryptoService;
import jakarta.persistence.Column;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    CryptoService cryptoService;

    @BeforeEach
    void tearUp(){
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("저장할 회원의 정보를 받아서 회원을 저장한다.")
    @Test
    void save(){
        //given
        String loginId = "loginId";
        String password = "password";
        String name = "name";
        LocalDate birth = LocalDate.of(2000,1,1);
        Gender gender = Gender.MAN;
        String street = "street";
        String city = "city";
        String zipCode = "zipCode";
        String detail = "detail";
        String phoneNumber = "010xxxxXXXX";
        String email = "email@email.com";
        School school = new School("xx초등학교", StudentType.ELEMENTARY,1);

        MemberSaveDto saveDto = new MemberSaveDto(loginId,password,name,
                birth,gender,street,city,zipCode,detail,phoneNumber,email,school);

        String encodedStreet = cryptoService.encryptAES(street);
        String encodedDetail = cryptoService.encryptAES(detail);
        String encodedPhoneNumber = cryptoService.encryptAES(phoneNumber);
        String encodedEmail = cryptoService.encryptAES(email);

        //when
        memberService.save(saveDto);
        List<Member> result = memberRepository.findAll();
        //then
        assertThat(result.get(0)).isNotNull()
                .extracting("loginId","name",
                        "birth","gender","phoneNumber","email","organization")
                .containsExactly(loginId,name, birth,gender,encodedPhoneNumber,encodedEmail,school);
        assertThat(passwordEncoder.matches(password,result.get(0).getPassword())).isTrue();
        assertThat(result.get(0).getAddress()).isNotNull()
                .extracting("street","city","zipCode","detail")
                .containsExactly(encodedStreet,city,zipCode,encodedDetail);
    }
}