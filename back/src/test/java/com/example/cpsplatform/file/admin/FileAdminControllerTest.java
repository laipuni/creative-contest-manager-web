package com.example.cpsplatform.file.admin;

import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.file.service.FileService;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.service.LoginFailService;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = FileAdminController.class)
class FileAdminControllerTest {

    @MockitoBean
    MemberRepository memberRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    LoginFailService loginFailService;

    @MockitoBean
    RegisterService registerService;

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

        Mockito.when(fileService.getTeamSolveFileIdsByContestId(contestId, TeamSolveType.SUBMITTED))
                .thenReturn(fileIds);

        //when
        //then
        mockMvc.perform(get("/api/admin/v1/contests/{contestId}/answers/zip-download", contestId)
                        .param("zipName", zipName)
                        .with(csrf())
                )
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
        mockMvc.perform(get("/api/admin/v1/contests/{contestId}/answers/zip-download", contestId)
                        .with(csrf())
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 단일 파일 다운로드를 요청하면 정상적으로 처리된다")
    @Test
    void testFileDownload() throws Exception {
        //given
        Long fileId = 1L;

        //when
        //then
        mockMvc.perform(get("/api/admin/files/{fileId}", fileId)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print());

    }

    @WithMockUser(roles = "USER")
    @DisplayName("ADMIN 권한이 없는 사용자는 단일 파일 다운로드에 접근할 수 없다")
    @Test
    void testFileDownloadUnauthorized() throws Exception {
        //given
        Long fileId = 1L;

        //when
        //then
        mockMvc.perform(get("/api/admin/files/{fileId}", fileId)
                        .with(csrf())
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

}