package com.example.cpsplatform.certificate.controller;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.certificate.admin.controller.CertificateAdminController;
import com.example.cpsplatform.certificate.admin.service.CertificateAdminService;
import com.example.cpsplatform.certificate.controller.response.SearchCertificateDto;
import com.example.cpsplatform.certificate.controller.response.SearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.service.CertificateService;
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
        SearchCertificateResponse mockResponse = createMockResponse();
        when(certificateService.searchCertificates(anyInt(), anyString(), isNull(), anyString()))
                .thenReturn(mockResponse);

        //when
        //then
        mockMvc.perform(
                        get("/api/v1/certificate")
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
        SearchCertificateResponse mockResponse = createMockResponse();
        when(certificateService.searchCertificates(anyInt(), anyString(), Mockito.any(CertificateType.class), anyString()))
                .thenReturn(mockResponse);

        //when
        //then
        mockMvc.perform(
                        get("/api/v1/certificate")
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
        SearchCertificateResponse mockResponse = createMockResponse();

        when(certificateService.searchCertificates(anyInt(), anyString(), isNull(), anyString()))
                .thenReturn(mockResponse);

        //when
        //then
        mockMvc.perform(
                        get("/api/v1/certificate")
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
        SearchCertificateResponse mockResponse = createMockResponse();

        when(certificateService.searchCertificates(anyInt(), anyString(), isNull(), anyString()))
                .thenReturn(mockResponse);

        //when
        //then
        mockMvc.perform(
                        get("/api/v1/certificate")
                                .param("order", "asc")
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200));
    }

    private SearchCertificateResponse createMockResponse() {
        List<SearchCertificateDto> certificateDtoList = new ArrayList<>();

        SearchCertificateDto dto1 = SearchCertificateDto.builder()
                .certificateId(1L)
                .title("인증서 제목 1")
                .certificateType(CertificateType.PRELIMINARY)
                .createdAt(LocalDateTime.now())
                .teamName("팀 이름 1")
                .build();

        SearchCertificateDto dto2 = SearchCertificateDto.builder()
                .certificateId(2L)
                .title("인증서 제목 2")
                .certificateType(CertificateType.FINAL)
                .createdAt(LocalDateTime.now())
                .teamName("팀 이름 2")
                .build();

        certificateDtoList.add(dto1);
        certificateDtoList.add(dto2);

        SearchCertificateResponse response = SearchCertificateResponse.builder()
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