package com.example.cpsplatform.problem.admin.controller;

import com.example.cpsplatform.problem.admin.controller.request.AddContestProblemRequest;
import com.example.cpsplatform.problem.admin.service.ContestProblemAdminService;
import com.example.cpsplatform.problem.domain.Section;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Arrays;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProblemAdminController.class)
class ProblemAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContestProblemAdminService contestProblemAdminService;

    @Test
    @DisplayName("대회 기출 문제 추가요청을 받는다.")
    @WithMockUser(roles = "ADMIN")
    void addContestProblem() throws Exception {
        //given
        AddContestProblemRequest request = new AddContestProblemRequest(
                1L,
                "문제 제목",
                Section.COMMON,
                "문제 설명",
                3
        );

        //테스트용 파일 생성
        MockMultipartFile pdfFile = new MockMultipartFile(
                "multipartFiles",
                "test.pdf",
                "application/pdf",
                "PDF 내용".getBytes()
        );

        MockMultipartFile pngFile = new MockMultipartFile(
                "multipartFiles",
                "file.pdf",
                "application/pdf",
                "PNG 이미지 데이터".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );


        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/admin/problems/real")
                        .file(requestPart)
                        .file(pdfFile)
                        .file(pngFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());

    }


    @Test
    @DisplayName("대회 기출 문제 추가요청을 했을 때, 등록할 문제의 제목 누락")
    @WithMockUser(roles = "ADMIN")
    void addContestProblem_MissingTitle() throws Exception {
        //given
        AddContestProblemRequest requestWithoutTitle = new AddContestProblemRequest(
                1L,
                "",  // 빈 제목
                Section.HIGH_NORMAL,
                "문제 설명",
                3
        );

        MockMultipartFile pdfFile = new MockMultipartFile(
                "multipartFiles",
                "test.pdf",
                "application/pdf",
                "PDF 내용.".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(requestWithoutTitle)
        );

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/admin/problems/real")
                        .file(requestPart)
                        .file(pdfFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("등록할 문제의 제목은 필수입니다."));
    }

    @Test
    @DisplayName("대회 기출 문제 추가요청을 했을 때, 대회 ID 누락")
    @WithMockUser(roles = "ADMIN")
    void addContestProblem_MissingContestId() throws Exception {
        //given
        AddContestProblemRequest requestWithoutContestId = new AddContestProblemRequest(
                null,
                "문제 제목",
                Section.ELEMENTARY_MIDDLE,
                "문제 설명",
                3
        );

        MockMultipartFile pdfFile = new MockMultipartFile(
                "multipartFiles",
                "test.pdf",
                "application/pdf",
                "PDF 내용".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(requestWithoutContestId)
        );

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/admin/problems/real")
                        .file(requestPart)
                        .file(pdfFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("등록할 문제의 대회 정보는 필수입니다."));
    }

    @Test
    @DisplayName("대회 기출 문제 추가요청을 했을 때, 파일 없음")
    @WithMockUser(roles = "ADMIN")
    void addContestProblem_NoFiles() throws Exception {
        //given
        AddContestProblemRequest request = new AddContestProblemRequest(
                1L,
                "문제 제목",
                Section.COMMON,
                "문제 설명",
                3
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );


        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/admin/problems/real")
                        .file(requestPart)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("대회 기출 문제 추가요청을 했을 때, 대용량 파일을 보내도 정상 응답한다.")
    @WithMockUser(roles = "ADMIN")
    void addContestProblem_LargeFile() throws Exception {
        //given
        AddContestProblemRequest request = new AddContestProblemRequest(
                1L,
                "문제 제목",
                Section.HIGH_NORMAL,
                "문제 설명",
                3
        );

        byte[] largeContent = new byte[1024 * 1024];
        Arrays.fill(largeContent, (byte) 1);

        MockMultipartFile largeFile = new MockMultipartFile(
                "multipartFiles",
                "large-file.pdf",
                "application/pdf",
                largeContent
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/admin/problems/real")
                        .file(requestPart)
                        .file(largeFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}