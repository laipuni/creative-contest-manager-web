package com.example.cpsplatform.member.service;

import com.example.cpsplatform.auth.controller.response.ProfilePasswordVerifyResponse;
import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.auth.service.SessionService;
import com.example.cpsplatform.auth.service.session.SessionType;
import com.example.cpsplatform.exception.PasswordMismatchException;
import com.example.cpsplatform.member.controller.response.MyProfileResponse;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.company.Company;
import com.example.cpsplatform.member.domain.organization.company.FieldType;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.dto.UpdateMyProfileDto;
import com.example.cpsplatform.security.encoder.CryptoService;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@Transactional
@SpringBootTest
class ProfileServiceTest {

    @MockitoBean
    AuthService authService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProfileService profileService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EntityManager entityManager;

    @Autowired
    CryptoService cryptoService;

    @MockitoBean
    SessionService sessionService;

    @Transactional
    @DisplayName("업데이트할 유저의 정보를 받아서 유저의 정보를 업데이트 합니다.")
    @Test
    void updateMyInformation(){
        //given
        //기존 유저 세팅
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        String email = "register@email.com";
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
        entityManager.flush();
        entityManager.clear();

        UpdateMyProfileDto profileDto = UpdateMyProfileDto.builder()
                .loginId(member.getLoginId())
                .name("updateName")
                .birth(LocalDate.now())
                .gender(Gender.WOMAN)
                .street("updateStreet")
                .city("updateCity")
                .zipCode("updateZipCode")
                .detail("updateDetail")
                .phoneNumber("01098769876")
                .email("update@email.com")
                .organizationType(FieldType.COMPUTER.getDescription())
                .organizationName("xxIT 기업")
                .position("대리")
                .build();
        //when
        profileService.updateMyInformation(profileDto);

        //then
        Member result = memberRepository.findAll().get(0);
        Assertions.assertThat(result)
                .extracting("name","birth","gender","phoneNumber","email")
                .containsExactly(
                        profileDto.getName(),
                        profileDto.getBirth(),
                        profileDto.getGender(),
                        cryptoService.encryptAES(profileDto.getPhoneNumber()),
                        cryptoService.encryptAES(profileDto.getEmail())
                );
        Assertions.assertThat(result.getAddress())
                .extracting("street","city","zipCode","detail")
                .containsExactly(
                        cryptoService.encryptAES(profileDto.getStreet()),
                        profileDto.getCity(),
                        profileDto.getZipCode(),
                        cryptoService.encryptAES(profileDto.getDetail())
                );
        Assertions.assertThat(result.getOrganization())
                .isInstanceOf(Company.class)// 직장인으로 변경
                .extracting("name","fieldType","position")
                .containsExactly(
                        profileDto.getOrganizationName(),
                        FieldType.COMPUTER,
                        profileDto.getPosition()
                );
    }

    @Transactional
    @DisplayName("업데이트할 유저의 정보에 해당하는 유저가 없을 경우 예외가 발생한다.")
    @Test
    void updateMyInformationWithNotExistMember(){
        //given
        UpdateMyProfileDto profileDto = UpdateMyProfileDto.builder()
                .loginId("invalidLoginId")
                .name("updateName")
                .birth(LocalDate.now())
                .gender(Gender.WOMAN)
                .street("updateStreet")
                .city("updateCity")
                .zipCode("updateZipCode")
                .detail("updateDetail")
                .phoneNumber("01098769876")
                .email("update@email.com")
                .organizationType(FieldType.COMPUTER.getDescription())
                .organizationName("xxIT 기업")
                .position("대리")
                .build();
        //when
        //then
        assertThatThrownBy(() -> profileService.updateMyInformation(profileDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("해당 유저는 존재하지 않습니다.");

    }

    @DisplayName("사용자 프로필을 반환한다.")
    @Test
    void getMyInformation(){
        //given
        String loginId = "loginId";
        Address address = new Address(
                cryptoService.encryptAES("street"),
                "city","zipCode",
                cryptoService.encryptAES("detail")
        );
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
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
        memberRepository.save(leader);

        //when
        MyProfileResponse response = profileService.getMyInformation(loginId,"session");

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

    @DisplayName("프로필을 조회하기 위한 비밀번호 검증 시, 비밀번호가 일치하지 않으면 예외가 발생한다.")
    @Test
    void verifyProfileUpdatePasswordWithMismatchPassword(){
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
        //then
        assertThatThrownBy(() -> profileService.verifyProfileUpdatePassword("invalidPassword",loginId))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessageMatching("비밀번호가 일치하지 않습니다.");

    }

    @DisplayName("프로필을 조회하기 위한 비밀번호 검증 시, 비밀번호가 일치하지 않으면 예외가 발생한다.")
    @Test
    void verifyProfileUpdatePassword(){
        //given
        String loginId = "loginId";
        Address address = new Address(
                cryptoService.encryptAES("street"),
                "city","zipCode",
                cryptoService.encryptAES("detail")
        );
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        String password = "1234";
        Member member = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode(password))
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

        String session = "session";
        Mockito.when(sessionService.storeSession(loginId, SessionType.PROFILE))
                .thenReturn(session);

        //when
        ProfilePasswordVerifyResponse response = profileService.verifyProfileUpdatePassword(password, loginId);
        //then
        assertThat(response.getSession()).isEqualTo(session);
    }
}