package com.example.cpsplatform.member.admin;

import com.example.cpsplatform.member.admin.controller.response.MemberDetailInfoResponse;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.encoder.CryptoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberAdminServiceTest {

    @Autowired
    MemberAdminService memberAdminService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CryptoService cryptoService;

    @DisplayName("관리자가 해당 유저의 정보를 복호화해서 조회한다.")
    @Test
    void getMemberDetailInfo(){
        //given
        String loginId = "loginId";
        Address address = new Address(
                cryptoService.encryptAES("street"),
                "city","zipCode",
                cryptoService.encryptAES("detail")
        );
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.of(2003,1,1))
                .email(cryptoService.encryptAES("email@email.com"))
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber(cryptoService.encryptAES("01012341234"))
                .name("테스트 인물")
                .organization(school)
                .build();
        memberRepository.save(member);

        //when
        MemberDetailInfoResponse response = memberAdminService.getMemberDetailInfo(member.getId());
        //then
        assertThat(response.getName()).isEqualTo("테스트 인물");
        assertThat(response.getBirth()).isEqualTo(LocalDate.of(2003, 1, 1));
        assertThat(response.getGender()).isEqualTo("남자");
        assertThat(response.getStreet()).isEqualTo("street");
        assertThat(response.getZipCode()).isEqualTo("zipCode");
        assertThat(response.getDetail()).isEqualTo("detail");
        assertThat(response.getPhoneNumber()).isEqualTo("01012341234");
        assertThat(response.getEmail()).isEqualTo("email@email.com");
        assertThat(response.getOrganizationType()).isEqualTo("대학생");
        assertThat(response.getOrganizationName()).isEqualTo("xx대학교");
        assertThat(response.getPosition()).isEqualTo("4");
    }

}