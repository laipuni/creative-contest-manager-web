package com.example.cpsplatform.teamsolve.controller;

import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.service.MemberService;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.security.service.LoginFailService;
import com.example.cpsplatform.teamsolve.controller.request.SubmitTeamAnswerRequest;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerDto;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerResponse;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
    void submitTeamAnswers() throws Exception {
        //given
        List<Long> problemIds = List.of(1L);

        SubmitTeamAnswerRequest request = new SubmitTeamAnswerRequest(problemIds);

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
    void submitTeamAnswersWithNullProblemIds() throws Exception {
        //given
        List<Long> problemIds = null;

        SubmitTeamAnswerRequest request = new SubmitTeamAnswerRequest(problemIds);

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

    @DisplayName("문제의 id와 파일의 사이즈가 맞지않을 경우 예외가 발생한다.")
    @Test
    void submitTeamAnswersWithFileAndProblemMismatchSize() throws Exception {
        //given
        List<Long> problemIds = Collections.emptyList();

        SubmitTeamAnswerRequest request = new SubmitTeamAnswerRequest(problemIds);

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
        GetTeamAnswerDto dto1 = new GetTeamAnswerDto(1L, "팀A", Section.ELEMENTARY_MIDDLE, LocalDateTime.now(), 2);
        dto1.setFileInfo(file1);


        GetTeamAnswerResponse response = new GetTeamAnswerResponse(List.of(dto1));

        //서비스 모의 설정
        when(answerSubmitService.getAnswer(anyLong(), anyString())).thenReturn(response);

        //When
        //Then
        mockMvc.perform(get("/api/contests/{contestId}/team-solves", contestId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.teamAnswerList").isArray())
                .andExpect(jsonPath("$.data.teamAnswerList[0].teamSolveId").value(1L))
                .andExpect(jsonPath("$.data.teamAnswerList[0].teamName").value("팀A"))
                .andExpect(jsonPath("$.data.teamAnswerList[0].section").value("ELEMENTARY_MIDDLE"))
                .andExpect(jsonPath("$.data.teamAnswerList[0].modifyCount").value(2))
                .andExpect(jsonPath("$.data.teamAnswerList[0].fileName").value("문제1_1.pdf"));
    }
}