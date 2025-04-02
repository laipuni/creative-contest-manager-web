package com.example.cpsplatform.member.service;

import com.example.cpsplatform.exception.PasswordMismatchException;
import com.example.cpsplatform.member.controller.request.MemberRegisterReqeust;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.service.dto.RegisterRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegisterServiceTest {

    @Autowired
    RegisterService registerService;

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

}