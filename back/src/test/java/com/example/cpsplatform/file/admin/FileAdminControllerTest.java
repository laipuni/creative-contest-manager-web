package com.example.cpsplatform.file.admin;

import com.example.cpsplatform.file.service.FileService;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.security.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = FileAdminController.class)
class FileAdminControllerTest {

    @MockitoBean
    FileDownloadService fileDownloadService;

    @MockitoBean
    FileService fileService;

    @Autowired
    MockMvc mockMvc;

    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 ZIP 다운로드를 요청하면 정상적으로 처리된다")
    @Test
    void testZipDownload() throws Exception {
        //given
        Long contestId = 1L;
        String zipName = "test-answers";
        List<Long> fileIds = List.of(1L, 2L, 3L);

        Mockito.when(fileService.getTeamSolveFileIdsByContestId(contestId))
                .thenReturn(fileIds);

        //when
        //then
        mockMvc.perform(get("/api/admin/v1/contests/{contestId}/answers/zip-download", contestId)
                        .param("zipName", zipName))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockUser(roles = "USER")
    @DisplayName("ADMIN 권한이 없는 사용자는 ZIP 다운로드에 접근할 수 없다")
    @Test
    void testZipDownloadUnauthorized() throws Exception {
        //given
        Long contestId = 1L;

        //when
        //then
        mockMvc.perform(get("/api/admin/v1/contests/{contestId}/answers/zip-download", contestId))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

}