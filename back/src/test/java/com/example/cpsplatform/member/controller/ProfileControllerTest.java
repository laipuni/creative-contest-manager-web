package com.example.cpsplatform.member.controller;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.member.controller.request.MyProfileUpdateRequest;
import com.example.cpsplatform.member.controller.response.MyProfileResponse;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.MemberService;
import com.example.cpsplatform.member.service.ProfileService;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.example.cpsplatform.member.domain.organization.school.StudentType.*;
import static com.example.cpsplatform.member.domain.organization.school.StudentType.COLLEGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(value = ProfileController.class)
class ProfileControllerTest {

    @MockitoBean
    MemberRepository memberRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    LoginFailService loginFailService;

    @MockitoBean
    RegisterService registerService;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    ProfileService profileService;

    @MockitoBean
    AuthService authService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("자신의 프로필 조회 요청을 해서 정상적으로 응답한다.")
    @Test
    void getMyInfo() throws Exception {
        //given
        //유저의 로그인 정보를 미리 세팅
        String loginId = "loginId";
        Member member = Member.builder()
                .loginId(loginId)
                .name("name")
                .password("password")
                .role(Role.USER)
                .build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                securityMember, null, securityMember.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        //자신의 프로필 응답값 Mock 처리
        MyProfileResponse response = MyProfileResponse.builder()
                .name("홍길동")
                .birth(LocalDate.of(1999, 1, 1))
                .gender("남성")
                .street("서울 강남구 역삼동")
                .zipCode("12345")
                .detail("101호")
                .city("서울")
                .phoneNumber("01012345678")
                .email("hong@example.com")
                .organizationType("학교")
                .organizationName("서울대학교")
                .position("학생")
                .build();

        Mockito.when(profileService.getMyInformation(anyString(),anyString())).thenReturn(response);

        String session = "session";

        //when
        //then
        mockMvc.perform(
                get("/api/members/my-profile")
                        .param("session", session)
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.birth").value("1999-01-01"))
                .andExpect(jsonPath("$.data.gender").value("남성"))
                .andExpect(jsonPath("$.data.street").value("서울 강남구 역삼동"))
                .andExpect(jsonPath("$.data.zipCode").value("12345"))
                .andExpect(jsonPath("$.data.city").value("서울"))
                .andExpect(jsonPath("$.data.detail").value("101호"))
                .andExpect(jsonPath("$.data.phoneNumber").value("01012345678"))
                .andExpect(jsonPath("$.data.email").value("hong@example.com"))
                .andExpect(jsonPath("$.data.organizationType").value("학교"))
                .andExpect(jsonPath("$.data.organizationName").value("서울대학교"))
                .andExpect(jsonPath("$.data.position").value("학생"))
                .andExpect(jsonPath("$.data.session").value(session));

    }

    @DisplayName("직업이 초,중,고,대학생일 경우일 때, 학년 정보를 숫자가 아닌 값으로 받을 경우 예외가 발생한다.")
    @Test
    void updateMyInfoWithNotNumberGrade() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest reqeust = getMyProfileUpdateRequest();
        reqeust.setOrganizationType("초등학생");
        reqeust.setOrganizationName("xxx초등학교");
        reqeust.setPosition("1학년");
        String content = objectMapper.writeValueAsString(reqeust);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("학년은 숫자만 입력해주세요."))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @DisplayName("직업이 초등학생일 때, 1~6의 범위를 넘을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithElementary() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest reqeust = getMyProfileUpdateRequest();
        reqeust.setOrganizationType("초등학생");
        reqeust.setOrganizationName("xxx초등학교");
        reqeust.setPosition("15");
        String content = objectMapper.writeValueAsString(reqeust);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(ELEMENTARY.getDescription() + "은 1부터 6까지 입력 가능합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("직업이 중학생일 때, 1~3의 범위를 넘을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithMiddle() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest reqeust = getMyProfileUpdateRequest();
        reqeust.setOrganizationType("중학생");
        reqeust.setOrganizationName("xxx중학교");
        reqeust.setPosition("15");
        String content = objectMapper.writeValueAsString(reqeust);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(MIDDLE.getDescription() + "은 1부터 3까지 입력 가능합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("직업이 고등학생일 때, 1~3의 범위를 넘을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithHigh() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest reqeust = getMyProfileUpdateRequest();
        reqeust.setOrganizationType("고등학생");
        reqeust.setOrganizationName("xxx고등학교");
        reqeust.setPosition("15");
        String content = objectMapper.writeValueAsString(reqeust);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(HIGH.getDescription() + "은 1부터 3까지 입력 가능합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("직업이 대학생일 때, 1~4의 범위를 넘을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithCollege() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest reqeust = getMyProfileUpdateRequest();
        reqeust.setOrganizationType("대학생");
        reqeust.setOrganizationName("xxx대학교");
        reqeust.setPosition("15");
        String content = objectMapper.writeValueAsString(reqeust);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(COLLEGE.getDescription() + "은 1부터 4까지 입력 가능합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("생년월일이 null값일 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithNullBirthDate() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setBirth(null); // 미래 날짜
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("생년월일은 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("미래 날짜를 생년월일로 입력할 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithFutureBirthDate() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setBirth(LocalDate.now().plusYears(1)); // 미래 날짜
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("생년월일은 과거 날짜여야 합니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }


    @DisplayName("휴대폰 번호 형식이 올바르지 않을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithInvalidPhoneNumber() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setPhoneNumber("01112345678"); // 010으로 시작하지 않음
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("휴대폰 번호는 010으로 시작하는 11자리 숫자여야 합니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("이메일 형식이 올바르지 않을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithInvalidEmail() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setEmail("invalid-email"); // @ 없음
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("이메일 형식이 올바르지 않습니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("이름이 비어있을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithEmptyRequiredField() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setName(""); // 이름 없음
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("이름은 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }


    @DisplayName("생년월일이 null일 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithNullBirth() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setBirth(null); // null 생년월일
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("생년월일은 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("성별이 null일 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithNullGender() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setGender(null); // null 성별
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("성별은 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("도로명 주소가 비어있을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithEmptyStreet() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setStreet(""); // 빈 도로명 주소
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("도로명 주소는 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("도시가 비어있을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithEmptyCity() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setCity(""); // 빈 도시명
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("도시명은 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("우편번호가 비어있을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithEmptyZipCode() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setZipCode(""); // 빈 우편번호
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("우편번호는 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("상세주소가 비어있을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithEmptyDetail() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setDetail(""); // 빈 상세주소
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("상세주소는 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("직업이 비어있을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithEmptyOrganizationType() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setOrganizationType(""); // 빈 직업
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("직업은 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("학교(소속) 이름이 비어있을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithEmptyOrganizationName() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();

        request.setOrganizationName(""); // 빈 학교(소속) 이름
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("학교(소속) 이름은 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("학년(부서)가 비어있을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithEmptyPosition() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setOrganizationType("컴퓨터");
        request.setOrganizationName("xx전자");
        request.setPosition(""); // 빈 학년(부서)
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("학년(부서)는 필수입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("세션값이 비어있을 경우, 예외가 발생한다.")
    @Test
    void updateMyInfoWithEmptySession() throws Exception {
        //given
        //로그인 정보 세팅
        Member member = Member.builder().loginId("loginId").name("name").password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MyProfileUpdateRequest request = getMyProfileUpdateRequest();
        request.setSession(""); //빈 세션 값
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/members/my-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("세션이 존재하지 않습니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("유저의 간단 프로필 정보를 요청받아 정상정으로 응답한다.")
    @Test
    void getUserInfo() throws Exception {
        //given
        String loginId = "loginId";
        String name = "name";
        Member member = Member.builder().loginId(loginId).name(name).password("password").role(Role.USER).build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        //when
        //then
        mockMvc.perform(
                        get("/api/members/user-info")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(OK.value()))
                .andExpect(jsonPath("$.data.loginId").value(loginId))
                .andExpect(jsonPath("$.data.name").value(name));
    }

    private MyProfileUpdateRequest getMyProfileUpdateRequest() {
        MyProfileUpdateRequest request = new MyProfileUpdateRequest();
        request.setName("홍길동");
        request.setCity("도시");
        request.setBirth(LocalDate.of(1990, 1, 1));
        request.setGender(Gender.MAN);
        request.setStreet("테스트 도로명 주소");
        request.setZipCode("12345");
        request.setDetail("상세주소 101호");
        request.setPhoneNumber("01012345678");
        request.setEmail("test@example.com");
        request.setOrganizationType("초등학생");
        request.setOrganizationName("xx학교");
        request.setPosition("1");
        request.setSession("session");
        return request;
    }
}