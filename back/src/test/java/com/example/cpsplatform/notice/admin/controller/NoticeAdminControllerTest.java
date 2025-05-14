package com.example.cpsplatform.notice.admin.controller;

import com.example.cpsplatform.auth.service.RegisterService;
import com.example.cpsplatform.file.admin.FileAdminController;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.notice.admin.controller.request.NoticeAddRequest;
import com.example.cpsplatform.notice.admin.controller.response.NoticeAddResponse;
import com.example.cpsplatform.notice.admin.service.NoticeFacadeService;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(SecurityConfig.class)
@WebMvcTest(controllers = NoticeAdminController.class)
class NoticeAdminControllerTest {

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
    NoticeFacadeService noticeFacadeService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @DisplayName("공지사항 등록 요청, 첨부파일 2개중에 1개가 실패한 경우")
    @Test
    void addNoticeWithFailedFile() throws Exception {
        //given
        //관리자 계정 세팅
        Member admin = Member.builder().loginId("admin").name("admin").password("password").role(Role.ADMIN).build();
        SecurityMember securityMember = new SecurityMember(admin);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        NoticeAddRequest request = new NoticeAddRequest("공지사항 제목", "공지사항 내용");

        MockMultipartFile file1 = new MockMultipartFile(
                "files", "file1.pdf", "application/pdf", "file1 content".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "file2.pdf", "application/pdf", "file2 content".getBytes()
        );
        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(request)
        );

        //응답값 mock 처리 후, mock 응답값 반환하도록 설정
        Long noticeId = 42L;
        NoticeAddResponse response = NoticeAddResponse.builder()
                .isSuccess(true)
                .message("공지사항은 등록되었지만, 첨부한 2개의 파일 중 1개는 업로드에 실패했습니다. (실패 파일: file2.pdf)")
                .noticeId(noticeId)
                .build();

        Mockito.when(noticeFacadeService.publishNotice(anyString(),anyString(),anyString(),any(FileSources.class)))
                .thenReturn(response);

        //when
        //then
        mockMvc.perform(multipart("/api/admin/notices")
                        .file(file1)
                        .file(file2)
                        .file(jsonPart)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.message").value("공지사항은 등록되었지만, 첨부한 2개의 파일 중 1개는 업로드에 실패했습니다. (실패 파일: file2.pdf)"))
                .andExpect(jsonPath("$.data.noticeId").value(noticeId));
    }

    @DisplayName("공지사항 등록 요청, 모든 첨부파일이 업로드 성공한 경우")
    @Test
    void addNoticeWitSuccess() throws Exception {
        //given
        //관리자 계정 세팅
        Member admin = Member.builder().loginId("admin").name("admin").password("password").role(Role.ADMIN).build();
        SecurityMember securityMember = new SecurityMember(admin);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        NoticeAddRequest request = new NoticeAddRequest("공지사항 제목", "공지사항 내용");

        MockMultipartFile file1 = new MockMultipartFile(
                "files", "file1.pdf", "application/pdf", "file1 content".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "file2.pdf", "application/pdf", "file2 content".getBytes()
        );
        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(request)
        );

        //응답값 mock 처리 후, mock 응답값 반환하도록 설정
        Long noticeId = 42L;
        NoticeAddResponse response = NoticeAddResponse.builder()
                .isSuccess(true)
                .message("공지사항 등록에 성공적으로 등록되었습니다.")
                .noticeId(noticeId)
                .build();

        Mockito.when(noticeFacadeService.publishNotice(anyString(),anyString(),anyString(),any(FileSources.class)))
                .thenReturn(response);

        //when
        //then
        mockMvc.perform(multipart("/api/admin/notices")
                        .file(file1)
                        .file(file2)
                        .file(jsonPart)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.message").value("공지사항 등록에 성공적으로 등록되었습니다."))
                .andExpect(jsonPath("$.data.noticeId").value(noticeId));
    }
}