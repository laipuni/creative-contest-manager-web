package com.example.cpsplatform.member.repository;

import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.security.encoder.CryptoService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    CryptoService cryptoService;

    @BeforeEach
    void tearUp(){
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("해당 아이디의 유저를 단건 조회한다.")
    @Test
    void findMemberByLoginId(){
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(loginId)
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
        //when
        Member result = memberRepository.findMemberByLoginId(loginId).get();

        //then
        assertThat(result).isNotNull()
                .extracting("loginId","password","role","birth",
                        "email","address","gender","phoneNumber","name","organization")
                .containsExactly(member.getLoginId(),member.getPassword(),member.getRole(),
                        member.getBirth(),member.getEmail(),member.getAddress(),member.getGender(),member.getPhoneNumber(),
                        member.getName(),member.getOrganization());
    }

    @DisplayName("해당 이메일의 유저를 단건 조회한다.")
    @Test
    void findMemberByEmail(){
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        String email = cryptoService.encryptAES("email@email.com");
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
        //when
        Member result = memberRepository.findMemberByEmailAndRole(email,Role.USER).get();

        //then
        assertThat(result).isNotNull()
                .extracting("loginId","password","role","birth",
                        "email","address","gender","phoneNumber","name","organization")
                .containsExactly(member.getLoginId(),member.getPassword(),member.getRole(),
                        member.getBirth(),member.getEmail(),member.getAddress(),member.getGender(),member.getPhoneNumber(),
                        member.getName(),member.getOrganization());
    }

    @DisplayName("해당 이메일의과 아이디에 해당하는 유저를 단건 조회한다.")
    @Test
    void findMemberByEmailAndLoginId(){
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        String email = cryptoService.encryptAES("email@email.com");
        String loginId = "loginId";
        Member member = Member.builder()
                .loginId(loginId)
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
        //when
        Member result = memberRepository.findMemberByEmailAndLoginIdAndRole(email, loginId,Role.USER).get();

        //then
        assertThat(result).isNotNull()
                .extracting("loginId","password","role","birth",
                        "email","address","gender","phoneNumber","name","organization")
                .containsExactly(member.getLoginId(),member.getPassword(),member.getRole(),
                        member.getBirth(),member.getEmail(),member.getAddress(),member.getGender(),member.getPhoneNumber(),
                        member.getName(),member.getOrganization());
    }

    @DisplayName("해당아이디의 유저가 존재하는지 확인한다.")
    @Test
    void existsByLoginId(){
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        String email = cryptoService.encryptAES("email@email.com");
        String loginId = "loginId";
        Member member = Member.builder()
                .loginId(loginId)
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

        //when
        boolean result = memberRepository.existsByLoginId(loginId);
        //then
        assertThat(result).isTrue();
    }

    @DisplayName("해당아이디의 유저가 존재하지 않을 경우 false 반환한다.")
    @Test
    void existsByLoginIdWithNotExist(){
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        String email = cryptoService.encryptAES("email@email.com");
        String loginId = "loginId";
        Member member = Member.builder()
                .loginId(loginId)
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

        String invalidLoginId = "InvalidLoginId";


        memberRepository.save(member);

        //when
        boolean result = memberRepository.existsByLoginId(invalidLoginId);
        //then
        assertThat(result).isFalse();
    }
}