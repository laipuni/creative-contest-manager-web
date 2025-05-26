package com.example.cpsplatform.admin.analysis;

import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionDto;
import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionResponse;
import com.example.cpsplatform.admin.analysis.service.OrganizationStatisticService;
import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.certificate.admin.controller.CertificateAdminController;
import com.example.cpsplatform.certificate.admin.service.CertificateAdminService;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = DataAnalysisController.class)
class DataAnalysisControllerTest {


    @MockitoBean
    AuthService authService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MemberRepository memberRepository;

    @MockitoBean
    LoginFailService loginFailService;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    OrganizationStatisticService organizationStatisticService;

    @WithMockUser(roles = "ADMIN")
    @DisplayName("조직 분포도 데이터 분석 요청 테스트")
    @Test
    void getOrganizationDistribution() throws Exception {
        //given
        List<OrganizationDistributionDto> mockDistributionList = List.of(
                OrganizationDistributionDto.builder()
                        .organizationType("COMPUTER")
                        .count(80L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("MEDIA")
                        .count(15L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("PUBLIC_SERVANT")
                        .count(25L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("MILITARY")
                        .count(10L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("SERVICE")
                        .count(30L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("ART")
                        .count(12L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("ETC")
                        .count(18L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("COLLEGE")
                        .count(40L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("HIGH")
                        .count(30L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("COMPUTER")
                        .count(50L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("SERVICE")
                        .count(20L)
                        .build(),
                OrganizationDistributionDto.builder()
                        .organizationType("ETC")
                        .count(15L)
                        .build()
        );
        mockDistributionList.forEach(OrganizationDistributionDto::setDescription);

        OrganizationDistributionResponse mockResponse = OrganizationDistributionResponse.builder()
                .distributionList(mockDistributionList)
                .build();

        Mockito.when(organizationStatisticService.getOrganizationDistribution())
                .thenReturn(mockResponse);
        //when

        //then
        mockMvc.perform(get("/api/admin/statistics/organization"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.distributionList[0].organizationType").value("COMPUTER"))
                .andExpect(jsonPath("$.data.distributionList[0].description").value("컴퓨터"))
                .andExpect(jsonPath("$.data.distributionList[0].count").value(80))
                .andExpect(jsonPath("$.data.distributionList[1].organizationType").value("MEDIA"))
                .andExpect(jsonPath("$.data.distributionList[1].description").value("언론"))
                .andExpect(jsonPath("$.data.distributionList[1].count").value(15))
                .andExpect(jsonPath("$.data.distributionList[2].organizationType").value("PUBLIC_SERVANT"))
                .andExpect(jsonPath("$.data.distributionList[2].description").value("공무원"))
                .andExpect(jsonPath("$.data.distributionList[2].count").value(25))
                .andExpect(jsonPath("$.data.distributionList[3].organizationType").value("MILITARY"))
                .andExpect(jsonPath("$.data.distributionList[3].description").value("군인"))
                .andExpect(jsonPath("$.data.distributionList[3].count").value(10))
                .andExpect(jsonPath("$.data.distributionList[4].organizationType").value("SERVICE"))
                .andExpect(jsonPath("$.data.distributionList[4].description").value("서비스업"))
                .andExpect(jsonPath("$.data.distributionList[4].count").value(30))
                .andExpect(jsonPath("$.data.distributionList[5].organizationType").value("ART"))
                .andExpect(jsonPath("$.data.distributionList[5].description").value("예술"))
                .andExpect(jsonPath("$.data.distributionList[5].count").value(12))
                .andExpect(jsonPath("$.data.distributionList[6].organizationType").value("ETC"))
                .andExpect(jsonPath("$.data.distributionList[6].description").value("기타"))
                .andExpect(jsonPath("$.data.distributionList[6].count").value(18))
                .andExpect(jsonPath("$.data.distributionList[7].organizationType").value("COLLEGE"))
                .andExpect(jsonPath("$.data.distributionList[7].description").value("대학생"))
                .andExpect(jsonPath("$.data.distributionList[7].count").value(40))
                .andExpect(jsonPath("$.data.distributionList[8].organizationType").value("HIGH"))
                .andExpect(jsonPath("$.data.distributionList[8].description").value("고등학생"))
                .andExpect(jsonPath("$.data.distributionList[8].count").value(30))
                .andExpect(jsonPath("$.data.distributionList[9].organizationType").value("COMPUTER"))
                .andExpect(jsonPath("$.data.distributionList[9].description").value("컴퓨터"))
                .andExpect(jsonPath("$.data.distributionList[9].count").value(50))
                .andExpect(jsonPath("$.data.distributionList[10].organizationType").value("SERVICE"))
                .andExpect(jsonPath("$.data.distributionList[10].description").value("서비스업"))
                .andExpect(jsonPath("$.data.distributionList[10].count").value(20))
                .andExpect(jsonPath("$.data.distributionList[11].organizationType").value("ETC"))
                .andExpect(jsonPath("$.data.distributionList[11].description").value("기타"))
                .andExpect(jsonPath("$.data.distributionList[11].count").value(15));
    }


}