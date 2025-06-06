package com.example.cpsplatform.teamsolve.controller;

import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.file.FileAccessService;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.MemberService;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.security.service.LoginFailService;
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.service.TeamService;
import com.example.cpsplatform.teamsolve.controller.request.SubmitTeamAnswerRequest;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerDto;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerResponse;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.service.AnswerSubmitService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = TeamSolveController.class)
class TeamSolveControllerTest {

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
    AnswerSubmitService answerSubmitService;

    @MockitoBean
    FileAccessService fileAccessService;

    @MockitoBean
    FileDownloadService fileDownloadService;

    @MockitoBean
    TeamService teamService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void tearUp(){
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

    @DisplayName("팀 답안지 제출 요청을 받아서 정상적으로 응답한다.")
    @Test
    void submitAnswerTemporary() throws Exception {
        //given
        Long problemId = 1L;

        SubmitTeamAnswerRequest request = new SubmitTeamAnswerRequest(problemId,"빈 내용");

        //테스트용 파일 생성
        MockMultipartFile multipartFiles = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF 내용".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/contests/{contestId}/team-solves",1L)
                        .file(requestPart)
                        .file(multipartFiles)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());

    }


    @DisplayName("팀 답안지 요청을 받았을 때, 답안지를 제출할 문제의 id가 없을 경우 에외가 발생한다.")
    @Test
    void submitAnswerTemporaryWithNullProblemIds() throws Exception {
        //given
        Long problemId = null;

        SubmitTeamAnswerRequest request = new SubmitTeamAnswerRequest(problemId,"빈 내용");

        //테스트용 파일 생성
        MockMultipartFile multipartFiles = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF 내용".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/contests/{contestId}/team-solves",1L)
                        .file(requestPart)
                        .file(multipartFiles)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("답안지를 제출할 문제들의 정보들은 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @DisplayName("해당 대회의 답안지 조회 요청을 받아 정상적으로 응답한다.")
    @Test
    void getAnswerSubmissionRequest() throws Exception {
        // Given
        Long contestId = 1L;

        File file1 = File.builder()
                .name("문제1_1.pdf")
                .originalName("문제1_1.pdf")
                .fileType(FileType.TEAM_SOLUTION)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(100L)
                .path("path")
                .build();

        //테스트용 DTO 생성
        GetTeamAnswerDto dto1 = new GetTeamAnswerDto(1L, "문제 풀이",1L,"팀A", Section.ELEMENTARY_MIDDLE, TeamSolveType.TEMP,LocalDateTime.now());
        dto1.setFileInfo(file1);


        GetTeamAnswerResponse response = new GetTeamAnswerResponse(0, SubmitStatus.TEMPORARY,List.of(dto1));

        //서비스 모의 설정
        when(answerSubmitService.getAnswer(anyLong(), anyString(), any())).thenReturn(response);

        //When
        //Then
        mockMvc.perform(get("/api/contests/{contestId}/team-solves", contestId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.finalSubmitCount").value(0))
                .andExpect(jsonPath("$.data.status").value("TEMPORARY"))
                .andExpect(jsonPath("$.data.teamAnswerList[0].teamSolveId").value(1L))
                .andExpect(jsonPath("$.data.teamAnswerList[0].teamName").value("팀A"))
                .andExpect(jsonPath("$.data.teamAnswerList[0].section").value("ELEMENTARY_MIDDLE"))
                .andExpect(jsonPath("$.data.teamAnswerList[0].fileName").value("문제1_1.pdf"));
    }

    @DisplayName("해당 팀의 답안지 파일 다운로드 요청을 받아 정상적으로 응답한다.")
    @Test
    void downloadTeamAnswer() throws Exception {
        //given
        Long teamId = 1L;
        Long fileId = 1L;
        //When
        //Then
        mockMvc.perform(get("/api/teams/{teamId}/files/{fileId}/answer/download", teamId,fileId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("임시 저장된 답안을 최종적으로 제출하는 요청을 받아 정상적으로 응답한다.")
    @Test
    void submitTeamAnswersComplete() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/contests/{contestId}/team-solves/complete",1L)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}