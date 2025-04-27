package com.example.cpsplatform.problem.admin.controller;

import com.example.cpsplatform.problem.admin.controller.request.DeleteContestProblemRequest;
import com.example.cpsplatform.problem.admin.controller.request.UpdateContestProblemRequest;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemDetailResponse;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemDto;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemFileDto;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemListResponse;
import com.example.cpsplatform.problem.admin.service.ContestProblemAdminService;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContestProblemAdminController.class)
class ContestProblemAdminControllerTest {

    @MockitoBean
    ContestProblemAdminService contestProblemAdminService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @WithMockUser(roles = "ADMIN")
    @DisplayName("대회 기출 문제 목록 조회가 성공적으로 이루어져야 한다")
    @Test
    void getContestProblemList() throws Exception {
        //given
        Long contestId = 1L;
        int page = 0;

        ContestProblemListResponse mockResponse = ContestProblemListResponse.builder()
                .totalPage(1)
                .page(page)
                .firstPage(0)
                .lastPage(0)
                .size(2)
                .problemList(List.of(
                        ContestProblemDto.builder()
                                .problemId(1L)
                                .title("문제 제목 1")
                                .season(16)
                                .section(Section.HIGH_NORMAL)
                                .problemOrder(1)
                                .build(),
                        ContestProblemDto.builder()
                                .problemId(2L)
                                .title("문제 제목 2")
                                .season(16)
                                .section(Section.COMMON)
                                .problemOrder(2)
                                .build()
                ))
                .build();

        when(contestProblemAdminService.findContestProblemList(contestId, page))
                .thenReturn(mockResponse);

        //when
        //then
        mockMvc.perform(get("/api/admin/v1/contests/{contestId}/problems", contestId)
                        .param("page", String.valueOf(page))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalPage").value(1))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.firstPage").value(0))
                .andExpect(jsonPath("$.data.lastPage").value(0))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.problemList").isArray())
                .andExpect(jsonPath("$.data.problemList.length()").value(2))
                .andExpect(jsonPath("$.data.problemList[0].problemId").value(1))
                .andExpect(jsonPath("$.data.problemList[0].title").value("문제 제목 1"))
                .andExpect(jsonPath("$.data.problemList[0].season").value(16))
                .andExpect(jsonPath("$.data.problemList[0].section").value("HIGH_NORMAL"))
                .andExpect(jsonPath("$.data.problemList[0].problemOrder").value(1))
                .andExpect(jsonPath("$.data.problemList[1].problemId").value(2))
                .andExpect(jsonPath("$.data.problemList[1].title").value("문제 제목 2"));
    }

    @Test
    @DisplayName("권한이 없는 사용자는 대회 기출 문제 목록을 조회할 수 없다")
    void getContestProblemListWithNotAdmin() throws Exception {
        //given
        Long contestId = 1L;
        int page = 0;

        //when
        //then
        mockMvc.perform(get("/api/admin/v1/contests/{contestId}/problems", contestId)
                        .param("page", String.valueOf(page))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("대회 기출 문제 상세 조회가 성공적으로 이루어져야 한다")
    @Test
    void getContestProblemDetail() throws Exception {
        // given
        Long contestId = 1L;
        Long problemId = 2L;

        LocalDateTime now = LocalDateTime.now();

        ContestProblemDetailResponse mockResponse = ContestProblemDetailResponse.builder()
                .problemId(problemId)
                .title("문제 제목 1")
                .season(16)
                .section(Section.HIGH_NORMAL)
                .content("문제 내용입니다.")
                .problemType(ProblemType.CONTEST)
                .problemOrder(1)
                .createdAt(now)
                .updatedAt(now)
                .fileList(List.of(
                        ContestProblemFileDto.builder()
                                .fileId(10L)
                                .originalFileName("문제1_1.pdf")
                                .createAt(now)
                                .build()
                ))
                .build();

        when(contestProblemAdminService.findContestProblemDetail(contestId, problemId))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/admin/v1/contests/{contestId}/problems/{problemId}", contestId, problemId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.problemId").value(problemId))
                .andExpect(jsonPath("$.data.title").value("문제 제목 1"))
                .andExpect(jsonPath("$.data.season").value(16))
                .andExpect(jsonPath("$.data.section").value("HIGH_NORMAL"))
                .andExpect(jsonPath("$.data.content").value("문제 내용입니다."))
                .andExpect(jsonPath("$.data.problemType").value("CONTEST"))
                .andExpect(jsonPath("$.data.problemOrder").value(1))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists())
                .andExpect(jsonPath("$.data.fileList").isArray())
                .andExpect(jsonPath("$.data.fileList[0].fileId").value(10))
                .andExpect(jsonPath("$.data.fileList[0].originalFileName").value("문제1_1.pdf"))
                .andExpect(jsonPath("$.data.fileList[0].createAt").exists());
    }

    @Test
    @DisplayName("권한이 없는 사용자는 대회 기출 문제 상세 조회를 할 수 없다")
    void getContestProblemDetailNotAdmin() throws Exception {
        //given
        Long contestId = 1L;
        Long problemId = 2L;
        int page = 0;

        //when
        //then
        mockMvc.perform(get("/api/admin/v1/contests/{contestId}/problems/{problemId}", contestId, problemId)
                        .param("page", String.valueOf(page))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회 기출 문제 수정이 성공적으로 이루어져야 한다")
    @Test
    void updateContestProblemSuccess() throws Exception {
        //given
        Long contestId = 1L;
        Long problemId = 2L;

        UpdateContestProblemRequest request = new UpdateContestProblemRequest(
                "수정된 문제 제목",
                Section.HIGH_NORMAL,
                "수정된 문제 내용",
                3,
                List.of(1L, 2L)
        );

        MockMultipartFile requestFile = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsString(request).getBytes()
        );

        MockMultipartFile testFile = new MockMultipartFile(
                "multipartFiles",
                "test-file.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        //when
        //then
        mockMvc.perform(multipart(HttpMethod.PUT,"/api/admin/contests/{contestId}/problems/{problemId}", contestId, problemId)
                        .file(requestFile)
                        .file(testFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200));

    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회 기출 문제 수정 시 제목이 비어있는 경우 예외를 반환한다")
    @Test
    void updateContestProblemFailWithEmptyTitle() throws Exception {
        //given
        Long contestId = 1L;
        Long problemId = 2L;

        UpdateContestProblemRequest request = new UpdateContestProblemRequest(
                "",  // 빈 제목
                Section.HIGH_NORMAL,
                "수정된 문제 내용",
                3,
                List.of(1L, 2L)
        );

        MockMultipartFile requestFile = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsString(request).getBytes()
        );

        //when
        //then
        mockMvc.perform(multipart(HttpMethod.PUT,"/api/admin/contests/{contestId}/problems/{problemId}", contestId, problemId)
                        .file(requestFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("등록할 문제의 제목은 필수입니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회 기출 문제 수정 시 섹션이 null인 경우 예외를 반환한다")
    @Test
    void updateContestProblemFailWithNullSection() throws Exception {
        //given
        Long contestId = 1L;
        Long problemId = 2L;

        UpdateContestProblemRequest request = new UpdateContestProblemRequest(
                "수정된 문제 제목",
                null,  // null 섹션
                "수정된 문제 내용",
                3,
                List.of(1L, 2L)
        );

        MockMultipartFile requestFile = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsString(request).getBytes()
        );

        //when
        //then
        mockMvc.perform(multipart(HttpMethod.PUT,"/api/admin/contests/{contestId}/problems/{problemId}", contestId, problemId)
                        .file(requestFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("등록할 문제의 섹션은 필수입니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회 기출 문제 수정 시 문제 번호가 0 이하인 경우 예외를 반환한다")
    @Test
    void updateContestProblemFailWithInvalidProblemOrder() throws Exception {
        //given
        Long contestId = 1L;
        Long problemId = 2L;

        UpdateContestProblemRequest request = new UpdateContestProblemRequest(
                "수정된 문제 제목",
                Section.HIGH_NORMAL,
                "수정된 문제 내용",
                0,  //유효하지 않은 문제 번호
                List.of(1L, 2L)
        );

        MockMultipartFile requestFile = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsString(request).getBytes()
        );

        //when
        //then
        mockMvc.perform(multipart(HttpMethod.PUT,"/api/admin/contests/{contestId}/problems/{problemId}", contestId, problemId)
                        .file(requestFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("출제할 문제의 번호는 0보다 큰 수여야 합니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회 기출 문제 삭제 시 삭제할 문제의 id가 null인 경우 예외를 반환한다")
    @Test
    void deleteContestProblemWithNullProblemId() throws Exception {
        //given
        DeleteContestProblemRequest request = new DeleteContestProblemRequest(
                null
        );

        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(delete("/api/admin/contests/problems")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("삭제할 문제의 정보는 필수입니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회 기출 문제 삭제 요청을 받아 정상적으로 응답한다.")
    @Test
    void deleteContestProblem() throws Exception {
        //given
        DeleteContestProblemRequest request = new DeleteContestProblemRequest(
                1L
        );

        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(delete("/api/admin/contests/problems")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200));
    }
}