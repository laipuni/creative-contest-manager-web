package com.example.cpsplatform.member.admin.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.member.admin.MemberAdminService;
import com.example.cpsplatform.member.admin.controller.response.MemberInfoListDto;
import com.example.cpsplatform.member.admin.controller.response.MemberInfoListResponse;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.company.Company;
import com.example.cpsplatform.member.domain.organization.company.FieldType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.MemberService;
import com.example.cpsplatform.member.service.ProfileService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(MemberAdminController.class)
public class MemberAdminControllerTest {

    @MockitoBean
    MemberRepository memberRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    LoginFailService loginFailService;

    @MockitoBean
    RegisterService registerService;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    AuthService authService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MemberAdminService memberAdminService;

    @WithMockUser(roles = "ADMIN")
    @DisplayName("아무 파라미터도 제공하지 않은 경우 기본 회원 목록을 조회한다")
    @Test
    void searchMemberWithNoParams() throws Exception {
        //given
        Company company = new Company("xx전자","대리", FieldType.COMPUTER);
        List<MemberInfoListDto> members = Arrays.asList(
                new MemberInfoListDto("user1", "홍길동", Role.USER, LocalDate.of(1990, 1, 1),
                        Gender.MAN, company, LocalDateTime.now()),
                new MemberInfoListDto("user2", "김철수", Role.USER, LocalDate.of(1992, 3, 15),
                        Gender.MAN, company, LocalDateTime.now())
        );

        MemberInfoListResponse response = MemberInfoListResponse.builder()
                .totalPage(1)
                .page(0)
                .firstPage(0)
                .lastPage(0)
                .size(2)
                .memberInfos(members)
                .build();

        when(memberAdminService.searchMember(any())).thenReturn(response);

        //when
        //then
        mockMvc.perform(
                        get("/api/admin/v1/members")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.memberInfos.length()").value(2))
                .andExpect(jsonPath("$.data.memberInfos[0].loginId").value("user1"))
                .andExpect(jsonPath("$.data.memberInfos[1].loginId").value("user2"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.totalPage").value(1));
    }
    @WithMockUser(roles = "ADMIN")
    @DisplayName("검색 타입과 검색어가 있는 경우 필터링된 회원 목록을 조회한다")
    @Test
    void searchMemberWithSearchTypeAndKeyword() throws Exception {
        //given
        Company company = new Company("xx전자","대리", FieldType.COMPUTER);
        List<MemberInfoListDto> members = Arrays.asList(
                new MemberInfoListDto("user1", "홍길동", Role.USER, LocalDate.of(1990, 1, 1),
                        Gender.MAN, company, LocalDateTime.now())
        );

        MemberInfoListResponse response = MemberInfoListResponse.builder()
                .totalPage(1)
                .page(0)
                .firstPage(0)
                .lastPage(0)
                .size(1)
                .memberInfos(members)
                .build();

        when(memberAdminService.searchMember(any())).thenReturn(response);

        // when & then
        mockMvc.perform(
                        get("/api/admin/v1/members")
                                .param("search", "홍길동")
                                .param("search_type", "name")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.memberInfos.length()").value(1))
                .andExpect(jsonPath("$.data.memberInfos[0].name").value("홍길동"));
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("성별 파라미터가 있는 경우 해당 성별의 회원 목록을 조회한다")
    @Test
    void searchMemberWithGender() throws Exception {
        //given
        Company company = new Company("xx전자","대리", FieldType.COMPUTER);
        List<MemberInfoListDto> members = Arrays.asList(
                new MemberInfoListDto("user1", "홍길동", Role.USER, LocalDate.of(1990, 1, 1),
                        Gender.MAN, company, LocalDateTime.now()),
                new MemberInfoListDto("user3", "박민수", Role.USER, LocalDate.of(1988, 5, 20),
                        Gender.MAN, company, LocalDateTime.now())
        );

        MemberInfoListResponse response = MemberInfoListResponse.builder()
                .totalPage(1)
                .page(0)
                .firstPage(0)
                .lastPage(0)
                .size(2)
                .memberInfos(members)
                .build();

        when(memberAdminService.searchMember(any())).thenReturn(response);

        //when
        //then
        mockMvc.perform(
                        get("/api/admin/v1/members")
                                .param("gender", "MAN")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.memberInfos.length()").value(2))
                .andExpect(jsonPath("$.data.memberInfos[0].gender").value("MAN"))
                .andExpect(jsonPath("$.data.memberInfos[1].gender").value("MAN"));
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("가입 일시 범위가 있는 경우 해당 기간에 가입한 회원 목록을 조회한다")
    @Test
    void searchMemberWithDateRange() throws Exception {
        //given
        Company company = new Company("xx전자","대리", FieldType.COMPUTER);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);

        List<MemberInfoListDto> members = Arrays.asList(
                new MemberInfoListDto("user1", "홍길동", com.example.cpsplatform.member.domain.Role.USER, LocalDate.of(1990, 1, 1),
                        Gender.MAN, company, now.minusDays(15))
        );

        MemberInfoListResponse response = MemberInfoListResponse.builder()
                .totalPage(1)
                .page(0)
                .firstPage(0)
                .lastPage(0)
                .size(1)
                .memberInfos(members)
                .build();

        when(memberAdminService.searchMember(any())).thenReturn(response);

        //when
        //then
        mockMvc.perform(
                        get("/api/admin/v1/members")
                                .param("start_date", oneMonthAgo.toString())
                                .param("end_date", now.toString())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.memberInfos.length()").value(1))
                .andExpect(jsonPath("$.data.memberInfos[0].loginId").value("user1"));
    }
}