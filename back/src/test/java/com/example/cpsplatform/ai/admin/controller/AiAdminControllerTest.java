package com.example.cpsplatform.ai.admin.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cpsplatform.ai.admin.controller.request.QuestionGenerateRequest;
import com.example.cpsplatform.ai.admin.service.AiAdminService;
import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.certificate.controller.CertificateController;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.service.LoginFailService;
import com.example.cpsplatform.team.controller.request.CreateTeamRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = AiAdminController.class)
class AiAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    AiAdminService aiAdminService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    MemberRepository memberRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    LoginFailService loginFailService;

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("문제 생성 요청 시 응답값을 정상적으로 반환한다.")
    @Test
    void generateQuestionsSuccess() throws Exception {
        // given
        QuestionGenerateRequest request = new QuestionGenerateRequest(
                "수학",
                "상",
                2
        );

        mockMvc.perform(post("/api/admin/ai/generate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("문제 생성 요청 시 요청개수가 1미만일 경우 에러가 발생한다.")
    @Test
    void generateQuestions() throws Exception {
        // given
        QuestionGenerateRequest request = new QuestionGenerateRequest(
                "논리",
                "상",
                0
        );

        mockMvc.perform(post("/api/admin/ai/generate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @WithMockUser(roles = "USER")
    @DisplayName("관리자가 아닌 사람이 문제생성을 요청할 경우 에러가 발생한다.")
    @Test
    void generateQuestionsByUser() throws Exception {
        QuestionGenerateRequest request = new QuestionGenerateRequest(
                "논리",
                "상",
                3
        );

        mockMvc.perform(post("/api/admin/ai/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }


}