package com.example.cpsplatform.member.controller;

import com.example.cpsplatform.member.controller.request.MemberRegisterReqeust;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static com.example.cpsplatform.member.domain.organization.school.StudentType.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(value = MemberController.class)
class MemberControllerTest {

    @MockitoBean
    MemberRepository memberRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    LoginFailService loginFailService;

    @MockitoBean
    RegisterService registerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("직업이 초,중,고,대학생일 경우일 때, 학년 정보를 숫자가 아닌 값으로 받을 경우 예외가 발생한다.")
    @Test
    void registerWithNotNumberGrade() throws Exception {
        //given
        MemberRegisterReqeust reqeust = getValidMemberRequest();
        reqeust.setOrganizationType("초등학생");
        reqeust.setOrganizationName("xxx초등학교");
        reqeust.setPosition("1학년");
        String content = objectMapper.writeValueAsString(reqeust);

        //when
        //then
        mockMvc.perform(
                post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(content)
        )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("학년은 숫자만 입력해주세요."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());

    }

    @DisplayName("직업이 초등학생일 때, 1~6의 범위를 넘을 경우, 예외가 발생한다.")
    @Test
    void registerWithElementary() throws Exception {
        //given
        MemberRegisterReqeust reqeust = getValidMemberRequest();
        reqeust.setOrganizationType("초등학생");
        reqeust.setOrganizationName("xxx초등학교");
        reqeust.setPosition("15");
        String content = objectMapper.writeValueAsString(reqeust);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ELEMENTARY.getDescription() + "은 1부터 6까지 입력 가능합니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("직업이 중학생일 때, 1~3의 범위를 넘을 경우, 예외가 발생한다.")
    @Test
    void registerWithMiddle() throws Exception {
        //given
        MemberRegisterReqeust reqeust = getValidMemberRequest();
        reqeust.setOrganizationType("중학생");
        reqeust.setOrganizationName("xxx중학교");
        reqeust.setPosition("15");
        String content = objectMapper.writeValueAsString(reqeust);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MIDDLE.getDescription() + "은 1부터 3까지 입력 가능합니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("직업이 고등학생일 때, 1~3의 범위를 넘을 경우, 예외가 발생한다.")
    @Test
    void registerWithHigh() throws Exception {
        //given
        MemberRegisterReqeust reqeust = getValidMemberRequest();
        reqeust.setOrganizationType("고등학생");
        reqeust.setOrganizationName("xxx고등학교");
        reqeust.setPosition("15");
        String content = objectMapper.writeValueAsString(reqeust);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(HIGH.getDescription() + "은 1부터 3까지 입력 가능합니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("직업이 대학생일 때, 1~4의 범위를 넘을 경우, 예외가 발생한다.")
    @Test
    void registerWithCollege() throws Exception {
        //given
        MemberRegisterReqeust reqeust = getValidMemberRequest();
        reqeust.setOrganizationType("대학생");
        reqeust.setOrganizationName("xxx대학교");
        reqeust.setPosition("15");
        String content = objectMapper.writeValueAsString(reqeust);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(COLLEGE.getDescription() + "은 1부터 4까지 입력 가능합니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("로그인 ID가 너무 짧을 경우, 예외가 발생한다.")
    @Test
    void registerWithShortLoginId() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setLoginId("abc"); // 4자 미만
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("로그인 ID는 4-12자 이내여야 합니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("로그인 ID가 너무 길 경우, 예외가 발생한다.")
    @Test
    void registerWithLongLoginId() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setLoginId("abcdefghijklmn"); // 12자 초과
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("로그인 ID는 4-12자 이내여야 합니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("로그인 ID에 영문자와 숫자 외의 문자가 포함될 경우, 예외가 발생한다.")
    @Test
    void registerWithInvalidLoginIdCharacters() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setLoginId("test@user"); // 특수문자 포함
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("로그인 ID는 영문자와 숫자만 가능합니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("비밀번호 확인이 빈칸일 경우 예외가 발생한다.")
    @Test
    void registerWithNotConfirmPassword() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setConfirmPassword("");
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("비밀번호확인은 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("생년월일이 null값일 경우, 예외가 발생한다.")
    @Test
    void registerWithNullBirthDate() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setBirth(null); // 미래 날짜
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("생년월일은 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("미래 날짜를 생년월일로 입력할 경우, 예외가 발생한다.")
    @Test
    void registerWithFutureBirthDate() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setBirth(LocalDate.now().plusYears(1)); // 미래 날짜
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("생년월일은 과거 날짜여야 합니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }


    @DisplayName("휴대폰 번호 형식이 올바르지 않을 경우, 예외가 발생한다.")
    @Test
    void registerWithInvalidPhoneNumber() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setPhoneNumber("01112345678"); // 010으로 시작하지 않음
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("휴대폰 번호는 010으로 시작하는 11자리 숫자여야 합니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("이메일 형식이 올바르지 않을 경우, 예외가 발생한다.")
    @Test
    void registerWithInvalidEmail() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setEmail("invalid-email"); // @ 없음
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 형식이 올바르지 않습니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("이름이 비어있을 경우, 예외가 발생한다.")
    @Test
    void registerWithEmptyRequiredField() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setName(""); // 이름 없음
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이름은 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("비밀번호확인이 비어있을 경우, 예외가 발생한다.")
    @Test
    void registerWithEmptyConfirmPassword() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setConfirmPassword(""); // 빈 비밀번호확인
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("비밀번호확인은 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("생년월일이 null일 경우, 예외가 발생한다.")
    @Test
    void registerWithNullBirth() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setBirth(null); // null 생년월일
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("생년월일은 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("성별이 null일 경우, 예외가 발생한다.")
    @Test
    void registerWithNullGender() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setGender(null); // null 성별
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("성별은 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("도로명 주소가 비어있을 경우, 예외가 발생한다.")
    @Test
    void registerWithEmptyStreet() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setStreet(""); // 빈 도로명 주소
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("도로명 주소는 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("도시가 비어있을 경우, 예외가 발생한다.")
    @Test
    void registerWithEmptyCity() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setCity(""); // 빈 도시명
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("도시명은 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("우편번호가 비어있을 경우, 예외가 발생한다.")
    @Test
    void registerWithEmptyZipCode() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setZipCode(""); // 빈 우편번호
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("우편번호는 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("상세주소가 비어있을 경우, 예외가 발생한다.")
    @Test
    void registerWithEmptyDetail() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setDetail(""); // 빈 상세주소
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상세주소는 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("이메일 인증 코드가 비어있을 경우, 예외가 발생한다.")
    @Test
    void registerWithEmptyConfirmEmailCode() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setConfirmEmailCode(""); // 빈 이메일 인증 코드
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 인증 코드는 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("직업이 비어있을 경우, 예외가 발생한다.")
    @Test
    void registerWithEmptyOrganizationType() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setOrganizationType(""); // 빈 직업
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("직업은 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("학교(소속) 이름이 비어있을 경우, 예외가 발생한다.")
    @Test
    void registerWithEmptyOrganizationName() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setOrganizationName(""); // 빈 학교(소속) 이름
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("학교(소속) 이름은 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("학년(부서)가 비어있을 경우, 예외가 발생한다.")
    @Test
    void registerWithEmptyPosition() throws Exception {
        //given
        MemberRegisterReqeust request = getValidMemberRequest();
        request.setOrganizationType("컴퓨터");
        request.setOrganizationName("xx전자");
        request.setPosition(""); // 빈 학년(부서)
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("학년(부서)는 필수입니다"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    private MemberRegisterReqeust getValidMemberRequest() {
        MemberRegisterReqeust request = new MemberRegisterReqeust();
        request.setLoginId("testuser");
        request.setPassword("pass1234");
        request.setConfirmPassword("pass1234");
        request.setName("홍길동");
        request.setCity("도시");
        request.setBirth(LocalDate.of(1990, 1, 1));
        request.setGender(Gender.MAN);
        request.setStreet("테스트 도로명 주소");
        request.setZipCode("12345");
        request.setDetail("상세주소 101호");
        request.setPhoneNumber("01012345678");
        request.setEmail("test@example.com");
        request.setConfirmEmailCode("123456");
        request.setOrganizationType("초등학생");
        request.setOrganizationName("xx학교");
        request.setPosition("1");
        return request;
    }

}