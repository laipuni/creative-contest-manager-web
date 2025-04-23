package com.example.cpsplatform.file.controller;

import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.contest.controller.service.ContestJoinService;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.security.service.LoginFailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(SecurityConfig.class)
@WebMvcTest(controllers = ContestProblemFileController.class)
class ContestProblemFileControllerTest {

    @MockitoBean
    MemberRepository memberRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    LoginFailService loginFailService;

    @MockitoBean
    RegisterService registerService;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private ContestJoinService contestJoinService;

    @MockitoBean
    private FileDownloadService fileDownloadService;

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("파일 다운로드 요청시 정상적으로 처리된다")
    void downloadContestProblem() throws Exception {
        //given
        //사용자 인증 정보를 주입
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

        Long contestId = 1L;
        Long fileId = 10L;

        //when
        //then
        mockMvc.perform(get("/api/contests/{contestId}/files/{fileId}", contestId, fileId)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 파일 다운로드 요청은 실패한다")
    void downloadContestProblemUnauthenticated() throws Exception {
        //given
        Long contestId = 1L;
        Long fileId = 10L;

        //when
        //then
        mockMvc.perform(get("/api/contests/{contestId}/files/{fileId}", contestId, fileId)
                        .with(csrf())
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

}