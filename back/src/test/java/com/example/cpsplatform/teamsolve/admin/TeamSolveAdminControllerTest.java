package com.example.cpsplatform.teamsolve.admin;

import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.file.FileAccessService;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.MemberService;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.service.LoginFailService;
import com.example.cpsplatform.team.service.TeamService;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListDto;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListResponse;
import com.example.cpsplatform.teamsolve.admin.service.TeamSolveAdminService;
import com.example.cpsplatform.teamsolve.controller.TeamSolveController;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.service.AnswerSubmitService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(SecurityConfig.class)
@WebMvcTest(controllers = TeamSolveAdminController.class)
class TeamSolveAdminControllerTest {

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
    TeamSolveAdminService teamSolveAdminService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("팀별 답안지 목록 조회 - 정상 응답")
    void getTeamSolveByTeam() throws Exception {
        Long teamId = 1L;
        TeamSolveType type = TeamSolveType.SUBMITTED;

        TeamSolveListDto dto1 = new TeamSolveListDto(
                101L,
                "문제 A",
                1,
                Section.ELEMENTARY_MIDDLE,
                1001L,
                TeamSolveType.SUBMITTED,
                LocalDateTime.now()
        );

        TeamSolveListDto dto2 = new TeamSolveListDto(
                102L,
                "문제 B",
                2,
                Section.COMMON,
                1002L,
                TeamSolveType.SUBMITTED,
                LocalDateTime.now()
        );

        TeamSolveListResponse responseData = new TeamSolveListResponse(List.of(dto1, dto2));

        when(teamSolveAdminService.getTeamSolveByTeam(eq(teamId), eq(TeamSolveType.SUBMITTED)))
                .thenReturn(responseData);


        mockMvc.perform(get("/api/admin/v1/teams/{teamId}/team-solves", teamId)
                        .param("team_solve_type", type.getKey())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                //첫 번째 DTO 필드 전부 검증
                .andExpect(jsonPath("$.data.teamSolveListDtos[0].problemId").value(101L))
                .andExpect(jsonPath("$.data.teamSolveListDtos[0].problemName").value("문제 A"))
                .andExpect(jsonPath("$.data.teamSolveListDtos[0].problemOrder").value(1))
                .andExpect(jsonPath("$.data.teamSolveListDtos[0].section").value("ELEMENTARY_MIDDLE"))
                .andExpect(jsonPath("$.data.teamSolveListDtos[0].teamSolveId").value(1001))
                .andExpect(jsonPath("$.data.teamSolveListDtos[0].type").value("SUBMITTED"))
                //두 번째 DTO 필드 전부 검증
                .andExpect(jsonPath("$.data.teamSolveListDtos[1].problemId").value(102))
                .andExpect(jsonPath("$.data.teamSolveListDtos[1].problemName").value("문제 B"))
                .andExpect(jsonPath("$.data.teamSolveListDtos[1].problemOrder").value(2))
                .andExpect(jsonPath("$.data.teamSolveListDtos[1].section").value("COMMON"))
                .andExpect(jsonPath("$.data.teamSolveListDtos[1].teamSolveId").value(1002))
                .andExpect(jsonPath("$.data.teamSolveListDtos[1].type").value("SUBMITTED"));

    }

}