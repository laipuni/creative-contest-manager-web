package com.example.cpsplatform.auth.controller;

import com.example.cpsplatform.auth.AuthService;
import com.example.cpsplatform.auth.controller.request.AuthCodeSendRequest;
import com.example.cpsplatform.member.repository.MemberRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthCodeController.class)
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
                .andExpect(status().isOk());
    }

    @DisplayName("인증코드를 수령할 사람의 정보, 전송 수단, 인증 수단을 값을 받는다.")
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

    @DisplayName("인증코드를 수령할 사람의 정보, 전송 수단, 인증 수단을 값을 받는다.")
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

    @DisplayName("인증코드를 수령할 사람의 정보, 전송 수단, 인증 수단을 값을 받는다.")
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

}