package com.example.cpsplatform.certificate.admin.controller;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.certificate.admin.controller.request.DeleteCertificateRequest;
import com.example.cpsplatform.certificate.admin.service.CertificateAdminService;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateDto;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateResponse;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("관리자 인증서 검색할 때, 기본 파라미터를 이용해서 검색한다.")
    void searchCertificatesWithDefaultParameters() throws Exception {
        //given
        List<AdminSearchCertificateDto> certificateDtos = List.of(
                AdminSearchCertificateDto.builder()
                        .certificateId(1L)
                        .certificateType(CertificateType.FINAL)
                        .title("본선 참여 진출증")
                        .teamName("A팀")
                        .loginId("loginId")
                        .name("name")
                        .season(16)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        AdminSearchCertificateResponse mockResponse = AdminSearchCertificateResponse.builder()
                .totalPage(1)
                .page(0)
                .firstPage(0)
                .lastPage(1)
                .size(10)
                .certificateDtoList(certificateDtos)
                .build();

        //when
        when(certificateAdminService.searchCertificates(any())).thenReturn(mockResponse);

        //then
        mockMvc.perform(get("/api/admin/certificates/search")
                        .param("page", "0")
                        .param("page_size", "10")
                        .param("order_type", "createdAt")
                        .param("direction", "asc")
                        .param("certificate_type", "")
                        .param("search_type", "")
                        .param("keyword", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.totalPage").value(1))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.firstPage").value(0))
                .andExpect(jsonPath("$.data.lastPage").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.certificateDtoList[0].certificateId").value(1))
                .andExpect(jsonPath("$.data.certificateDtoList[0].certificateType").value("FINAL"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].title").value("본선 참여 진출증"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].teamName").value("A팀"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].createdAt").exists())
                .andExpect(jsonPath("$.data.certificateDtoList[0].loginId").value("loginId"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].name").value("name"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].season").value(16));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("특정 인증서 타입에 맞는 인증서를 검색한 뒤, 특정 타입의 인증서들을 반환한다.")
    void searchCertificatesWithCertificateType() throws Exception {
        //given
        List<AdminSearchCertificateDto> certificateDtos = List.of(
                AdminSearchCertificateDto.builder()
                        .certificateId(1L)
                        .certificateType(CertificateType.FINAL)
                        .title("본선 참여 진출증")
                        .teamName("A팀")
                        .loginId("loginId")
                        .name("name")
                        .season(16)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        AdminSearchCertificateResponse mockResponse = AdminSearchCertificateResponse.builder()
                .totalPage(1)
                .page(0)
                .firstPage(0)
                .lastPage(1)
                .size(5)
                .certificateDtoList(certificateDtos)
                .build();

        //when
        when(certificateAdminService.searchCertificates(any())).thenReturn(mockResponse);

        //then
        mockMvc.perform(get("/api/admin/certificates/search")
                        .param("page", "0")
                        .param("page_size", "10")
                        .param("order_type", "createdAt")
                        .param("direction", "asc")
                        .param("certificate_type", "final")
                        .param("search_type", "")
                        .param("keyword", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.totalPage").value(1))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.firstPage").value(0))
                .andExpect(jsonPath("$.data.lastPage").value(1))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.certificateDtoList[0].certificateId").value(1))
                .andExpect(jsonPath("$.data.certificateDtoList[0].certificateType").value("FINAL"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].title").value("본선 참여 진출증"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].teamName").value("A팀"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].createdAt").exists())
                .andExpect(jsonPath("$.data.certificateDtoList[0].loginId").value("loginId"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].name").value("name"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].season").value(16));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("인증서를 검색 키워드받아 검색한 뒤 적절한 응답값을 반환한다.")
    void searchCertificatesWithKeyword() throws Exception {
        //given
        List<AdminSearchCertificateDto> certificateDtos = List.of(
                AdminSearchCertificateDto.builder()
                        .certificateId(1L)
                        .certificateType(CertificateType.PRELIMINARY)
                        .title("예선 확인증")
                        .teamName("A팀")
                        .loginId("loginId")
                        .name("name")
                        .season(16)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        AdminSearchCertificateResponse mockResponse = AdminSearchCertificateResponse.builder()
                .totalPage(1)
                .page(0)
                .firstPage(0)
                .lastPage(1)
                .size(3)
                .certificateDtoList(certificateDtos)
                .build();

        //when
        when(certificateAdminService.searchCertificates(any())).thenReturn(mockResponse);

        //then
        mockMvc.perform(get("/api/admin/certificates/search")
                        .param("page", "0")
                        .param("page_size", "10")
                        .param("order_type", "createdAt")
                        .param("direction", "asc")
                        .param("certificate_type", "")
                        .param("search_type", "title")
                        .param("keyword", "프로젝트"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.totalPage").value(1))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.firstPage").value(0))
                .andExpect(jsonPath("$.data.lastPage").value(1))
                .andExpect(jsonPath("$.data.size").value(3))
                .andExpect(jsonPath("$.data.certificateDtoList[0].certificateId").value(1))
                .andExpect(jsonPath("$.data.certificateDtoList[0].certificateType").value("PRELIMINARY"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].title").value("예선 확인증"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].teamName").value("A팀"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].createdAt").exists())
                .andExpect(jsonPath("$.data.certificateDtoList[0].loginId").value("loginId"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].name").value("name"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].season").value(16));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("페이지 파라미터를 받아서 페이지에 맞는 응답값을 반환한다.")
    void searchCertificates_pagingParameters() throws Exception {
        //given
        List<AdminSearchCertificateDto> certificateDtos = List.of(
                AdminSearchCertificateDto.builder()
                        .certificateId(1L)
                        .certificateType(CertificateType.PRELIMINARY)
                        .title("예선 확인증")
                        .teamName("A팀")
                        .loginId("loginId")
                        .name("name")
                        .season(16)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        AdminSearchCertificateResponse mockResponse = AdminSearchCertificateResponse.builder()
                .totalPage(5)
                .page(2)
                .firstPage(0)
                .lastPage(4)
                .size(50)
                .certificateDtoList(certificateDtos)
                .build();

        //when
        when(certificateAdminService.searchCertificates(any())).thenReturn(mockResponse);

        //then
        mockMvc.perform(get("/api/admin/certificates/search")
                        .param("page", "2")
                        .param("page_size", "10")
                        .param("order_type", "createdAt")
                        .param("direction", "desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.totalPage").value(5))
                .andExpect(jsonPath("$.data.page").value(2))
                .andExpect(jsonPath("$.data.firstPage").value(0))
                .andExpect(jsonPath("$.data.lastPage").value(4))
                .andExpect(jsonPath("$.data.size").value(50))
                .andExpect(jsonPath("$.data.certificateDtoList[0].certificateId").value(1))
                .andExpect(jsonPath("$.data.certificateDtoList[0].certificateType").value("PRELIMINARY"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].title").value("예선 확인증"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].teamName").value("A팀"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].createdAt").exists())
                .andExpect(jsonPath("$.data.certificateDtoList[0].loginId").value("loginId"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].name").value("name"))
                .andExpect(jsonPath("$.data.certificateDtoList[0].season").value(16));

    }
}