package com.example.cpsplatform.team.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.security.service.LoginFailService;
import com.example.cpsplatform.team.controller.request.CreateTeamRequest;
import com.example.cpsplatform.team.service.TeamService;
import com.example.cpsplatform.team.service.dto.MyTeamInfoByContestDto;
import com.example.cpsplatform.team.service.dto.MyTeamMemberDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Import(SecurityConfig.class)
@WebMvcTest(TeamController.class)
class TeamControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    LoginFailService loginFailService;

    @MockitoBean
    RegisterService registerService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupSecurityContext() {
        Member member = Member.builder()
                .loginId("yi")
                .name("kimi")
                .password("password")
                .role(Role.USER)
                .build();
        SecurityMember securityMember = new SecurityMember(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                securityMember, null, securityMember.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @DisplayName("팀 생성 요청이 들어왔을 때 성공적으로 생성된 팀 ID를 반환한다.")
    @WithMockUser(username = "yi", roles = "USER")
    @Test
    void create_team_success() throws Exception {
        // given
        CreateTeamRequest request = new CreateTeamRequest(
                "팀입니다",
                1L,
                List.of("one", "two")
        );

        when(teamService.createTeam(any(), any())).thenReturn(100L);

        // when & then
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(100L))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200));
    }

    @DisplayName("팀명이 6자를 초과하면 에러가 발생한다(400 에러).")
    @WithMockUser(username = "yi", roles = "USER")
    @Test
    void create_team_name_over_six() throws Exception {
        // given
        CreateTeamRequest request = new CreateTeamRequest(
                "일이삼사오육칠", // 7자 이상
                1L,
                List.of("one", "two")
        );

        // when & then
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("특정 콘테스트의 내 팀 정보를 조회할 수 있다")
    @WithMockUser(username = "testuser")
    void getMyTeamByContest_Success() throws Exception {
        //given
        MyTeamMemberDto member1 = MyTeamMemberDto.builder()
                .memberId(1L)
                .loginId("user1")
                .name("사용자1")
                .build();

        MyTeamMemberDto member2 = MyTeamMemberDto.builder()
                .memberId(2L)
                .loginId("user2")
                .name("사용자2")
                .build();

        //테스트용 Team DTO
        LocalDateTime LocalDateTime = java.time.LocalDateTime.now();
        MyTeamInfoByContestDto teamInfoDto = MyTeamInfoByContestDto.builder()
                .teamId(1L)
                .teamName("테스트 팀")
                .leaderLoginId("leader")
                .leaderName("팀장")
                .members(List.of(member1, member2))
                .createdAt(LocalDateTime)
                .contestId(1L)
                .build();

        Long contestId = 1L;
        when(teamService.getMyTeamInfoByContest(anyLong(), anyString()))
                .thenReturn(teamInfoDto);

        //when
        //then
        mockMvc.perform(
                        get("/api/contests/{contestId}/my-team", contestId)
                                .contentType("application/json")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.teamId").value(1))
                .andExpect(jsonPath("$.data.teamName").value("테스트 팀"))
                .andExpect(jsonPath("$.data.leaderLoginId").value("leader"))
                .andExpect(jsonPath("$.data.leaderName").value("팀장"))
                .andExpect(jsonPath("$.data.members").isArray())
                .andExpect(jsonPath("$.data.members[0].memberId").value(1L))
                .andExpect(jsonPath("$.data.members[0].loginId").value("user1"))
                .andExpect(jsonPath("$.data.members[0].name").value("사용자1"))
                .andExpect(jsonPath("$.data.members[1].memberId").value(2L))
                .andExpect(jsonPath("$.data.members[1].loginId").value("user2"))
                .andExpect(jsonPath("$.data.members[1].name").value("사용자2"))
                .andExpect(jsonPath("$.data.contestId").value(1L));
    }


}