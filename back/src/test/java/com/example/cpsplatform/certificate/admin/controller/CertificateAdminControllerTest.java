package com.example.cpsplatform.certificate.admin.controller;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.certificate.admin.controller.request.DeleteCertificateRequest;
import com.example.cpsplatform.certificate.admin.service.CertificateAdminService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = CertificateAdminController.class)
class CertificateAdminControllerTest {

    @MockitoBean
    CertificateAdminService certificateAdminService;

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

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회에 참여한 팀원들의 예선 참여 확인증을 일괄 발급요청을 받아 정상적으로 응답한다.")
    @Test
    void batchPreliminaryCertificates() throws Exception {
        //given
        Long contestId = 1L;

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/contests/{contestId}/certificates/preliminary/batch",contestId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockUser()
    @DisplayName("대회에 참여한 팀원들의 예선 참여 확인증을 일괄 발급요청은 관리자만 요청할 수 있다.")
    @Test
    void batchPreliminaryCertificatesWithNotAdmin() throws Exception {
        //given
        Long contestId = 1L;

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/contests/{contestId}/certificates/preliminary/batch",contestId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("확인증 삭제 요청을 받아서 확인증을 삭제한다.")
    @Test
    void deleteCertificate() throws Exception {
        //given
        DeleteCertificateRequest request = new DeleteCertificateRequest(1L);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/certificates")
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

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("확인증 삭제 요청을 받았을 때, 삭제할 확인증의 id가 없을 경우 예외가 발생한다.")
    @Test
    void deleteCertificateWithNotCertificateId() throws Exception {
        //given
        DeleteCertificateRequest request = new DeleteCertificateRequest(null);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/certificates")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("삭제할 확인증의 정보는 필수 입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockUser(roles = {"USER"})
    @DisplayName("확인증 삭제 요청을 받았을 때, 관리자가 아닐경우 예외가 발생한다.")
    @Test
    void deleteCertificateWithNotAdmin() throws Exception {
        //given
        DeleteCertificateRequest request = new DeleteCertificateRequest(1L);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/certificates")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isForbidden());

    }
}