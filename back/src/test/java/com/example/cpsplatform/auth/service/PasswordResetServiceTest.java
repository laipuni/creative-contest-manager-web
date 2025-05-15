package com.example.cpsplatform.auth.service;

import com.example.cpsplatform.auth.service.dto.PasswordResetDto;
import com.example.cpsplatform.exception.PasswordMismatchException;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.example.cpsplatform.auth.service.session.RedisSessionService.PASSWORD_SESSION_KEY;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PasswordResetServiceTest {

    //테스트용으로 id를 선언하고, 각각의 테스트에서 사용한 뒤 해당 id의 key값들만 제거하면 됨
    public static final String testId = "testId";

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    PasswordResetService passwordResetService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void tearUp(){
        memberRepository.deleteAll();
    }

    @DisplayName("비밀번호를 재설정할 때, 비밀번호 재설정 세션이 유효하지 않다면 예외가 발생한다.")
    @Test
    void resetPasswordWithInvalidSession(){
        //given
        String session = UUID.randomUUID().toString();
        String invalidSession = "invalidSession";
        String newPassword = "newPassword";
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(PASSWORD_SESSION_KEY + testId,session);

        PasswordResetDto passwordResetDto = new PasswordResetDto(invalidSession,testId,newPassword,newPassword);

        //when
        //then
        Assertions.assertThatThrownBy(() -> passwordResetService.resetPassword(passwordResetDto))
                .isInstanceOf(InvalidSessionException.class)
                .hasMessageMatching("세션이 만료되었습니다.");
    }

    @DisplayName("비밀번호를 재설정할 때, 비밀번호 재설정과 비밀번호 확인이 일치하지 않으면 예외가 발생한다.")
    @Test
    void resetPasswordWithMistMatchPassword(){
        //given
        String session = UUID.randomUUID().toString();
        String newPassword = "newPassword";
        String mismatchPassword = "mismatchPassword";
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(PASSWORD_SESSION_KEY + testId,session);

        PasswordResetDto passwordResetDto = new PasswordResetDto(session,testId,newPassword,mismatchPassword);
        //when
        //then
        Assertions.assertThatThrownBy(() -> passwordResetService.resetPassword(passwordResetDto))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessageMatching("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }

    @DisplayName("기존 비밀번호에서 새로운 비밀번호로 변경한다.")
    @Test
    void resetPassword(){
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(testId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("사람 이름")
                .organization(school)
                .build();

        memberRepository.save(member);

        String session = UUID.randomUUID().toString();
        String newPassword = "newPassword";
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(PASSWORD_SESSION_KEY + testId,session);

        PasswordResetDto passwordResetDto = new PasswordResetDto(session,testId,newPassword,newPassword);

        //when
        passwordResetService.resetPassword(passwordResetDto);
        List<Member> result = memberRepository.findAll();
        //then
        assertThat(result.get(0).getLoginId()).isEqualTo(testId);
        Assertions.assertThat(passwordEncoder.matches(newPassword, result.get(0).getPassword())).isTrue();

    }

}