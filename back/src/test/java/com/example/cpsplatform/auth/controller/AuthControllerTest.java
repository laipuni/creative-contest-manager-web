package com.example.cpsplatform.auth.controller;

import com.example.cpsplatform.auth.controller.request.*;
import com.example.cpsplatform.auth.controller.response.PasswordConfirmResponse;
import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.auth.controller.response.FindIdResponse;
import com.example.cpsplatform.auth.service.PasswordResetService;
import com.example.cpsplatform.auth.service.dto.PasswordConfirmDto;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.auth.service.dto.FindIdDto;
import com.example.cpsplatform.member.service.ProfileService;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockitoBean
    AuthService authService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MemberRepository memberRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    LoginFailService loginFailService;

    @MockitoBean
    ProfileService profileService;

    @MockitoBean
    RegisterService registerService;

    @MockitoBean
    PasswordResetService passwordResetService;

    @DisplayName("인증코드를 수령할 사람의 정보, 전송 수단, 인증 수단을 값을 받는다.")
    @Test
    void sendAuthCode() throws Exception {
        //given
        String recipient = "email@email.com";
        String senderType = "email";
        String strategyType = "register";
        AuthCodeSendRequest request = new AuthCodeSendRequest(recipient,senderType,strategyType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/send-auth-code")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @DisplayName("인증코드를 수령할 사람의 정보가 없을 경우 예외로 응답한다.")
    @Test
    void sendAuthCodeWithBlankRecipient() throws Exception {
        //given
        String recipient = "";
        String senderType = "email";
        String strategyType = "register";
        AuthCodeSendRequest request = new AuthCodeSendRequest(recipient,senderType,strategyType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/send-auth-code")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("인증 정보는 필수입니다."));

    }

    @DisplayName("유효하지 않은 전송 수단일 경우 예외를 발생한다.")
    @Test
    void sendAuthCodeWithBlankSenderType() throws Exception {
        //given
        String recipient = "email@email.com";
        String senderType = "";
        String strategyType = "register";
        AuthCodeSendRequest request = new AuthCodeSendRequest(recipient,senderType,strategyType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/send-auth-code")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("전송 수단 선택은 필수입니다."));

    }

    @DisplayName("유효하지 않은 인증 수단일 경우 예외로 응답한다.")
    @Test
    void sendAuthCodeWithBlankStrategyType() throws Exception {
        //given
        String recipient = "email@email.com";
        String senderType = "email";
        String strategyType = "";
        AuthCodeSendRequest request = new AuthCodeSendRequest(recipient,senderType,strategyType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/send-auth-code")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("인증 수단은 필수입니다."));

    }

    @DisplayName("인증코드를 수령할 사람의 정보가 없을 경우 예외로 응답한다.")
    @Test
    void verifyAuthCodeWithNotRecipient() throws Exception {
        //given
        String recipient = "";
        String authCode = "1234";
        String strategyType = "register";

        AuthCodeVerifyRequest request = new AuthCodeVerifyRequest(recipient,authCode,strategyType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/verify-register-code")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("인증 정보는 필수입니다."));

    }

    @DisplayName("인증코드를 수령할 사람의 정보가 없을 경우 예외로 응답한다.")
    @Test
    void verifyAuthCodeWithNotAuthCode() throws Exception {
        //given
        String recipient = "email@email.com";
        String authCode = "";
        String strategyType = "register";

        AuthCodeVerifyRequest request = new AuthCodeVerifyRequest(recipient,authCode,strategyType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/verify-register-code")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("인증코드는 필수입니다."));

    }

    @DisplayName("인증코드를 수령할 사람의 정보가 없을 경우 예외로 응답한다.")
    @Test
    void verifyAuthCodeWithNotStrategyType() throws Exception {
        //given
        String recipient = "email@email.com";
        String authCode = "1234";
        String strategyType = "";

        AuthCodeVerifyRequest request = new AuthCodeVerifyRequest(recipient,authCode,strategyType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/verify-register-code")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("인증 수단은 필수입니다."));

    }

    @DisplayName("인증코드를 수령할 사람의 정보가 없을 경우 예외로 응답한다.")
    @Test
    void verifyAuthCode() throws Exception {
        //given
        String recipient = "email@email.com";
        String authCode = "1234";
        String strategyType = "register";

        AuthCodeVerifyRequest request = new AuthCodeVerifyRequest(recipient,authCode,strategyType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/verify-register-code")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @DisplayName("아이디 찾기 요청을 받았을 때, 인증코드를 수령할 사람의 정보가 없을 경우 예외로 응답한다.")
    @Test
    void findLoginIdWithNotRecipient() throws Exception {
        //given
        String recipient = "";
        String authCode = "authCode";
        String senderType = "findId";
        FindIdRequest request = new FindIdRequest(recipient,authCode,senderType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/find-id")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("인증 정보는 필수입니다."));

    }

    @DisplayName("아이디 찾기 요청을 받았을 때, 인증코드가 없을 경우 예외로 응답한다.")
    @Test
    void findLoginIdWithNotAuthCode() throws Exception {
        //given
        String recipient = "email@email.com";
        String authCode = "";
        String senderType = "findId";
        FindIdRequest request = new FindIdRequest(recipient,authCode,senderType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/find-id")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("인증 코드는 필수입니다."));

    }

    @DisplayName("아이디 찾기 요청을 받았을 때, 인증코드를 수령할 사람의 정보가 없을 경우 예외로 응답한다.")
    @Test
    void findLoginIdWithNotSenderType() throws Exception {
        //given
        String recipient = "email@email.com";
        String authCode = "authCode";
        String senderType = "";
        FindIdRequest request = new FindIdRequest(recipient,authCode,senderType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/find-id")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("전송 수단 선택은 필수입니다."));

    }

    @DisplayName("인증코드 수령한 사람의 정보, 인증코드, 전송수단을 받아 해당 정보와 일치하는 유저의 아이디를 반환한다.")
    @Test
    void findLoginId() throws Exception {
        //given
        String recipient = "email@email.com";
        String authCode = "authCode";
        String senderType = "findId";
        String loginId = "testId";

        FindIdRequest request = new FindIdRequest(recipient,authCode,senderType);
        String content = objectMapper.writeValueAsString(request);

        FindIdResponse response = FindIdResponse.builder()
                .loginId(loginId)
                .build();
        when(registerService.findId(Mockito.any(FindIdDto.class)))
                .thenReturn(response);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/find-id")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.loginId").value(response.getLoginId()));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 요청할 때, 인증 코드를 수령할 사람의 정보가 없을 경우 예외로 응답한다.")
    @Test
    void requestPasswordAuthCodeWithNotRecipient() throws Exception {
        //given
        String recipient = "";
        String senderType = "password_auth";
        String loginId = "testId";

        PasswordSendRequest request = new PasswordSendRequest(loginId,recipient,senderType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset/request")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("인증 정보는 필수입니다."));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 요청할 때, 전송 수단이 없을 경우 예외로 응답한다.")
    @Test
    void requestPasswordAuthCodeWithNotSenderType() throws Exception {
        //given
        String recipient = "email@email.com";
        String senderType = "";
        String loginId = "testId";

        PasswordSendRequest request = new PasswordSendRequest(loginId,recipient,senderType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset/request")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("전송 수단 선택은 필수입니다."));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 요청할 때, 인증 코드를 수령할 사람의 아이디가 없을 경우 예외로 응답한다.")
    @Test
    void requestPasswordAuthCodeWithNotLoginId() throws Exception {
        //given
        String recipient = "email@email.com";
        String senderType = "findId";
        String loginId = "";

        PasswordSendRequest request = new PasswordSendRequest(loginId,recipient,senderType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset/request")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("비밀번호를 찾을 아이디는 필수입니다."));

    }

    @DisplayName("비밀번호 요청을 받아 비밀번호 인증코드를 전송한다.")
    @Test
    void requestPasswordAuthCode() throws Exception {
        //given
        String recipient = "email@email.com";
        String senderType = "password_auth";
        String loginId = "testId";

        PasswordSendRequest request = new PasswordSendRequest(loginId,recipient,senderType);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset/request")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());


    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인할 때, 인증 코드를 수령할 사람의 아이디가 없을 경우 예외로 응답한다.")
    @Test
    void confirmPasswordAuthCodeWithNotLoginId() throws Exception {
        //given
        String loginId = "";
        String recipient = "email@email.com";
        String senderType = "email";
        String authCode = "authCode";

        PasswordConfirmRequest request = new PasswordConfirmRequest(loginId,recipient,senderType,authCode);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset/confirm")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("비밀번호를 찾을 아이디는 필수입니다."));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인할 때, 인증 코드를 수령할 사람의 정보가 없을 경우 예외로 응답한다.")
    @Test
    void confirmPasswordAuthCodeWithrecipient() throws Exception {
        //given
        String loginId = "loginId";
        String recipient = "";
        String senderType = "email";
        String authCode = "authCode";

        PasswordConfirmRequest request = new PasswordConfirmRequest(loginId,recipient,senderType,authCode);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset/confirm")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("인증 정보는 필수입니다."));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인할 때, 전송 수단 정보가 없을 경우 예외로 응답한다.")
    @Test
    void confirmPasswordAuthCodeWithNotSenderType() throws Exception {
        //given
        String loginId = "loginId";
        String recipient = "email@email.com";
        String senderType = "";
        String authCode = "authCode";

        PasswordConfirmRequest request = new PasswordConfirmRequest(loginId,recipient,senderType,authCode);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset/confirm")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("전송 수단 선택은 필수입니다."));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인할 때, 인증 코드를 수령할 사람의 아이디가 없을 경우 예외로 응답한다.")
    @Test
    void confirmPasswordAuthCodeWithNotAuthCode() throws Exception {
        //given
        String loginId = "loginId";
        String recipient = "email@email.com";
        String senderType = "email";
        String authCode = "";

        PasswordConfirmRequest request = new PasswordConfirmRequest(loginId,recipient,senderType,authCode);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset/confirm")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("인증 코드는 필수입니다."));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인하고 비밀번호 재설정 세션을 반환한다.")
    @Test
    void confirmPasswordAuthCode() throws Exception {
        //given
        String loginId = "loginId";
        String recipient = "email@email.com";
        String senderType = "email";
        String authCode = "authCode";
        PasswordConfirmRequest request = new PasswordConfirmRequest(loginId,recipient,senderType,authCode);
        String content = objectMapper.writeValueAsString(request);

        String session = UUID.randomUUID().toString();
        PasswordConfirmResponse response = new PasswordConfirmResponse(session);

        Mockito.when(passwordResetService.confirmPasswordAuthCode(Mockito.any(PasswordConfirmDto.class)))
                        .thenReturn(response);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset/confirm")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.session").value(session));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인할 때, 인증 코드를 수령할 사람의 아이디가 없을 경우 예외로 응답한다.")
    @Test
    void resetPasswordWithNotSession() throws Exception {
        //given
        String session = "";
        String loginId = "loginId";
        String resetPassword = "password";
        String confirmPassword = "password";

        PasswordResetRequest request = new PasswordResetRequest(session,loginId,resetPassword,confirmPassword);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("유효하지 않는 접근입니다."));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인할 때, 인증 코드를 수령할 사람의 아이디가 없을 경우 예외로 응답한다.")
    @Test
    void resetPasswordWithNotLoginId() throws Exception {
        //given
        String session = "session";
        String loginId = "";
        String resetPassword = "password";
        String confirmPassword = "password";

        PasswordResetRequest request = new PasswordResetRequest(session,loginId,resetPassword,confirmPassword);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("비밀번호를 재설정 할 아이디는 필수입니다."));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인할 때, 인증 코드를 수령할 사람의 아이디가 없을 경우 예외로 응답한다.")
    @Test
    void resetPasswordWithNotResetPassword() throws Exception {
        //given
        String session = "session";
        String loginId = "loginId";
        String resetPassword = "";
        String confirmPassword = "password";

        PasswordResetRequest request = new PasswordResetRequest(session,loginId,resetPassword,confirmPassword);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("비밀번호는 4-8자 이내여야 합니다"));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인할 때, 인증 코드를 수령할 사람의 아이디가 없을 경우 예외로 응답한다.")
    @Test
    void resetPasswordWithInvalidResetPassword() throws Exception {
        //given
        String session = "session";
        String loginId = "loginId";
        String resetPassword = "resetPassword1234"; // 4~8자의 범위를 넘는 비밀번호
        String confirmPassword = "resetPassword1234";

        PasswordResetRequest request = new PasswordResetRequest(session,loginId,resetPassword,confirmPassword);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("비밀번호는 4-8자 이내여야 합니다"));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인할 때, 인증 코드를 수령할 사람의 아이디가 없을 경우 예외로 응답한다.")
    @Test
    void resetPasswordWithNotConfirmPassword() throws Exception {
        //given
        String session = "session";
        String loginId = "loginId";
        String resetPassword = "password";
        String confirmPassword = "";

        PasswordResetRequest request = new PasswordResetRequest(session,loginId,resetPassword,confirmPassword);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("비밀번호확인은 필수입니다"));

    }

    @DisplayName("비밀번호 재설정 인증 코드를 확인할 때, 인증 코드를 수령할 사람의 아이디가 없을 경우 예외로 응답한다.")
    @Test
    void resetPassword() throws Exception {
        //given
        String session = "session";
        String loginId = "loginId";
        String resetPassword = "password";
        String confirmPassword = "password";

        PasswordResetRequest request = new PasswordResetRequest(session,loginId,resetPassword,confirmPassword);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/password-reset")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());

    }
}