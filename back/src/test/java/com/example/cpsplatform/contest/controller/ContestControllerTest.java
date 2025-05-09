package com.example.cpsplatform.contest.controller;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.contest.admin.service.ContestAdminService;
import com.example.cpsplatform.contest.controller.response.LatestContestResponse;
import com.example.cpsplatform.contest.service.ContestJoinService;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = ContestController.class)
class ContestControllerTest {

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
    ContestAdminService contestAdminService;

    @MockitoBean
    ContestJoinService contestJoinService;

    @WithMockUser
    @DisplayName("사용자가 대회에 참가 신청하면 성공 응답이 반환된다")
    @Test
    void joinContest() throws Exception {
        //given
        //로그인 인증 정보를 설정
        Long contestId = 1L;
        Member member = Member.builder().role(Role.USER).name("name").loginId("loginId").password("password").build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                securityMember, null, securityMember.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        //when
        //then
        mockMvc.perform(post("/api/contests/{contestId}/join", contestId)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @WithMockUser
    @Test
    @DisplayName("최신 대회 정보를 조회하면 해당 정보가 반환된다")
    void getLatestContestInfo() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        LatestContestResponse response = LatestContestResponse.builder()
                .contestId(1L)
                .season(16)
                .registrationStartAt(now.minusDays(5))
                .registrationEndAt(now.minusDays(1))
                .startTime(now)
                .endTime(now.plusDays(1))
                .build();

        when(contestJoinService.getLatestContestInfo()).thenReturn(response);

        //when
        //then
        mockMvc.perform(get("/api/contests/latest")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.contestId").value(1L))
                .andExpect(jsonPath("$.data.season").value(16))
                .andExpect(jsonPath("$.data.registrationStartAt").exists())
                .andExpect(jsonPath("$.data.registrationEndAt").exists())
                .andExpect(jsonPath("$.data.startTime").exists())
                .andExpect(jsonPath("$.data.endTime").exists())
                .andDo(print());
    }

}