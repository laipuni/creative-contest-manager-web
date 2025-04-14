package com.example.cpsplatform.problem.admin.controller;

import com.example.cpsplatform.problem.admin.controller.response.ContestProblemDto;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemListResponse;
import com.example.cpsplatform.problem.admin.service.ContestProblemAdminService;
import com.example.cpsplatform.problem.domain.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContestProblemAdminController.class)
class ContestProblemAdminControllerTest {

    @MockitoBean
    ContestProblemAdminService contestProblemAdminService;

    @Autowired
    MockMvc mockMvc;

    @WithMockUser(roles = "ADMIN")
    @DisplayName("대회 기출 문제 목록 조회가 성공적으로 이루어져야 한다")
    @Test
    void getContestProblemList() throws Exception {
        //given
        Long contestId = 1L;
        int page = 0;

        ContestProblemListResponse mockResponse = ContestProblemListResponse.builder()
                .totalPage(1)        // 총 페이지 수
                .page(page)          // 현재 페이지
                .firstPage(0)        // 시작 페이지
                .lastPage(0)         // 마지막 페이지
                .size(2)             // 총 요소 수
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
}