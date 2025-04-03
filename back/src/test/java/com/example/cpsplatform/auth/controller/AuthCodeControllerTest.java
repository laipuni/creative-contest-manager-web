package com.example.cpsplatform.auth.controller;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.auth.controller.request.AuthCodeSendRequest;
import com.example.cpsplatform.auth.controller.request.FindIdRequest;
import com.example.cpsplatform.auth.controller.request.PasswordSendRequest;
import com.example.cpsplatform.auth.controller.response.FindIdResponse;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.member.service.dto.FindIdDto;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthCodeControllerTest {

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
    RegisterService registerService;



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

}