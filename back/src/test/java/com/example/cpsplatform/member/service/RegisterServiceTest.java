package com.example.cpsplatform.member.service;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.auth.controller.response.FindIdResponse;
import com.example.cpsplatform.exception.PasswordMismatchException;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.auth.service.dto.FindIdDto;
import com.example.cpsplatform.auth.service.dto.RegisterRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
class RegisterServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RegisterService registerService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    AuthService authService;


    @DisplayName("비밀번호 확인과 비밀번호가 다를 경우, 예외가 발생한다.")
    @Test
    void registerWithMisMatchPassword(){
        //given
        String loginId = "loginId";
        String password = "password";
        String confirmPassword = "password";
        String name = "name";
        LocalDate birth = LocalDate.of(2000,1,1);
        Gender gender = Gender.MAN;
        Address address = new Address("street","zipCode","detail");
        String phoneNumber = "010xxxxXXXX";
        String email = "email@email.com";
        String confirmEmailCode = "authCode";
        School school = new School("xx초등학교", StudentType.ELEMENTARY,1);

        RegisterRequestDto request = new RegisterRequestDto(
                loginId, password, confirmPassword, name, birth, gender,
                address, phoneNumber, email, confirmEmailCode, school
        );

        //when
        //then
        Assertions.assertThatThrownBy(() ->registerService.register(request))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessageMatching("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }

    @DisplayName("이메일 인증 방식으로 아이디를 찾을 경우 이메일로 회원의 아이디를 조회한다.")
    @Test
    void findId(){
        //given
        Address address = new Address("street","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        String email = "email@email.com";
        Member member = Member.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email(email)
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("사람 이름")
                .organization(school)
                .build();
        memberRepository.save(member);



        String authCode = "1234";
        FindIdDto findIdDto = new FindIdDto(email, authCode,"email");

        Mockito.when(authService.verifyAuthCode(anyString(),anyString(),anyString()))
                .thenReturn(true);

        //when
        FindIdResponse result = registerService.findId(findIdDto);

        //then
        Assertions.assertThat(result.getLoginId()).isNotNull()
                .isEqualTo(member.getLoginId());
    }

}