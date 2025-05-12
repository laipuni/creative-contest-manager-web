package com.example.cpsplatform.certificate.controller;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateDto;
import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.dto.UserSearchCertificateCond;
import com.example.cpsplatform.certificate.service.CertificateService;
import com.example.cpsplatform.certificate.service.dto.DownloadCertificateResult;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = CertificateController.class)
class CertificateControllerTest {

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
    CertificateService certificateService;

    @BeforeEach
    void setupSecurityContext() {
        Member member = Member.builder()
                .loginId("loginId")
                .name("name")
                .password("password")
                .role(Role.USER)
                .build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                securityMember, null, securityMember.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @WithMockUser
    @DisplayName("기본 검색 파라미터로 인증서 목록을 조회한다")
    @Test
    void searchCertificatesWithDefaultParameters() throws Exception {
        //given
        UserSearchCertificateResponse mockResponse = createMockResponse();
        when(certificateService.searchCertificates(any(UserSearchCertificateCond.class)))
                .thenReturn(mockResponse);

        //when
        //then
        mockMvc.perform(
                        get("/api/v1/certificates")
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.certificateDtoList").isArray())
                .andExpect(jsonPath("$.data.certificateDtoList[0].certificateType").value("PRELIMINARY"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].certificateId").value(1))
                .andExpect(jsonPath("$.data.certificateDtoList[0].title").value("인증서 제목 1"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].teamName").value("팀 이름 1"));

    }

    @WithMockUser
    @DisplayName("특정 인증서 타입으로 인증서 목록을 조회한다")
    @Test
    void searchCertificatesWithType() throws Exception {
        //given
        UserSearchCertificateResponse mockResponse = createMockResponse();
        when(certificateService.searchCertificates(any(UserSearchCertificateCond.class)))
                .thenReturn(mockResponse);

        //when
        //then
        mockMvc.perform(
                        get("/api/v1/certificates")
                                .param("type", "PRELIMINARY")
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.certificateDtoList").isArray())
                .andExpect(jsonPath("$.data.certificateDtoList[1].certificateType").value("FINAL"))
                .andExpect(jsonPath("$.data.certificateDtoList[1].certificateId").value(2))
                .andExpect(jsonPath("$.data.certificateDtoList[1].title").value("인증서 제목 2"))
                .andExpect(jsonPath("$.data.certificateDtoList[1].teamName").value("팀 이름 2"))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.totalPage").value(1));
    }

    @WithMockUser
    @DisplayName("페이지 번호를 지정하여 인증서 목록을 조회한다")
    @Test
    void searchCertificatesWithPage() throws Exception {
        //given
        UserSearchCertificateResponse mockResponse = createMockResponse();

        when(certificateService.searchCertificates(any(UserSearchCertificateCond.class)))
                .thenReturn(mockResponse);

        //when
        //then
        mockMvc.perform(
                        get("/api/v1/certificates")
                                .param("page", "1")
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.page").value(1));
    }

    @WithMockUser
    @DisplayName("정렬 방향을 지정하여 인증서 목록을 조회한다")
    @Test
    void searchCertificatesWithOrder() throws Exception {
        //given
        UserSearchCertificateResponse mockResponse = createMockResponse();

        when(certificateService.searchCertificates(any(UserSearchCertificateCond.class)))
                .thenReturn(mockResponse);

        //when
        //then
        mockMvc.perform(
                        get("/api/v1/certificates")
                                .param("order", "asc")
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200));
    }

    @WithMockUser
    @DisplayName("정렬 방향을 지정하여 인증서 목록을 조회한다")
    @Test
    void downloadCertificate() throws Exception {
        //given
        byte[] bytes = new byte[100];
        DownloadCertificateResult result = DownloadCertificateResult.of(bytes,"16회 창의력 경진대회 확인증");

        Mockito.when(certificateService.downloadCertificate(Mockito.anyString(),Mockito.anyLong()))
                        .thenReturn(result);

        //when
        //then
        mockMvc.perform(
                        get("/api/certificate/{certificateId}",1L)
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    private UserSearchCertificateResponse createMockResponse() {
        List<UserSearchCertificateDto> certificateDtoList = new ArrayList<>();

        UserSearchCertificateDto dto1 = UserSearchCertificateDto.builder()
                .certificateId(1L)
                .title("인증서 제목 1")
                .certificateType(CertificateType.PRELIMINARY)
                .createdAt(LocalDateTime.now())
                .teamName("팀 이름 1")
                .build();

        UserSearchCertificateDto dto2 = UserSearchCertificateDto.builder()
                .certificateId(2L)
                .title("인증서 제목 2")
                .certificateType(CertificateType.FINAL)
                .createdAt(LocalDateTime.now())
                .teamName("팀 이름 2")
                .build();

        certificateDtoList.add(dto1);
        certificateDtoList.add(dto2);

        UserSearchCertificateResponse response = UserSearchCertificateResponse.builder()
                .certificateDtoList(certificateDtoList)
                .firstPage(0)
                .lastPage(10)
                .size(2)
                .totalPage(1)
                .page(1)
                .build();

        return response;
    }

}