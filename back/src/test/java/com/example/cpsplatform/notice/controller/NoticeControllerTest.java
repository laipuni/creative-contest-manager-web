package com.example.cpsplatform.notice.controller;

import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.notice.admin.controller.response.NoticeDetailFileDto;
import com.example.cpsplatform.notice.admin.controller.response.NoticeDetailResponse;
import com.example.cpsplatform.notice.controller.response.UserNoticeDetailFileDto;
import com.example.cpsplatform.notice.controller.response.UserNoticeDetailResponse;
import com.example.cpsplatform.notice.controller.response.UserNoticeSearchDto;
import com.example.cpsplatform.notice.controller.response.UserNoticeSearchResponse;
import com.example.cpsplatform.notice.service.NoticeService;
import com.example.cpsplatform.notice.service.NoticeUserFacadeService;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = NoticeController.class)
class NoticeControllerTest {

    @MockitoBean
    MemberRepository memberRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    LoginFailService loginFailService;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    NoticeService noticeService;

    @MockitoBean
    NoticeUserFacadeService noticeUserFacadeService;

    @Autowired
    MockMvc mockMvc;

    @WithMockUser(roles = "User")
    @Test
    @DisplayName("유저용 공지사항 검색 API - 검색 조건 없이 전체 조회")
    void searchNoticesWithoutKeyword() throws Exception {
        // given
        List<UserNoticeSearchDto> notices = List.of(
                UserNoticeSearchDto.builder()
                        .noticeId(1L)
                        .title("공지사항 제목")
                        .viewCount(123L)
                        .writer("admin")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        UserNoticeSearchResponse response = UserNoticeSearchResponse.builder()
                .totalPage(1)
                .page(0)
                .firstPage(0)
                .lastPage(1)
                .size(1)
                .noticeSearchDtoList(notices)
                .build();

        //when
        when(noticeService.searchNotice(any())).thenReturn(response);

        //then
        mockMvc.perform(get("/api/notices/search")
                        .param("page", "0")
                        .param("page_size", "10")
                        .param("keyword", "")
                        .param("search_type", "")
                        .param("order", "desc")
                        .param("order_type", "createdAt"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.totalPage").value(1))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.firstPage").value(0))
                .andExpect(jsonPath("$.data.lastPage").value(1))
                .andExpect(jsonPath("$.data.size").value(1))
                .andExpect(jsonPath("$.data.noticeSearchDtoList[0].noticeId").value(1))
                .andExpect(jsonPath("$.data.noticeSearchDtoList[0].title").value("공지사항 제목"))
                .andExpect(jsonPath("$.data.noticeSearchDtoList[0].viewCount").value(123))
                .andExpect(jsonPath("$.data.noticeSearchDtoList[0].writer").value("admin"))
                .andExpect(jsonPath("$.data.noticeSearchDtoList[0].createdAt").exists());
    }

    @WithMockUser(roles = "User")
    @Test
    @DisplayName("유저가 공지사항 상세 조회를 성공적으로 수행한다.")
    void getNoticeDetail_success() throws Exception {
        // given
        Long noticeId = 1L;
        List<UserNoticeDetailFileDto> fileDtos = List.of(
                UserNoticeDetailFileDto.builder()
                        .fileId(10L)
                        .fileName("example.pdf")
                        .build()
        );

        UserNoticeDetailResponse response = UserNoticeDetailResponse.builder()
                .noticeId(noticeId)
                .title("공지사항 제목")
                .viewCount(123L)
                .writer("관리자")
                .writerEmail("admin@example.com")
                .createAt(LocalDateTime.of(2025, 5, 18, 10, 30))
                .updatedAt(LocalDateTime.of(2025, 5, 18, 11, 0))
                .content("공지사항 본문")
                .fileList(fileDtos)
                .build();

        when(noticeUserFacadeService.retrieveNotice(noticeId)).thenReturn(response);

        //when
        //then
        mockMvc.perform(get("/api/notices/{noticeId}", noticeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.noticeId").value(noticeId))
                .andExpect(jsonPath("$.data.title").value("공지사항 제목"))
                .andExpect(jsonPath("$.data.viewCount").value(123))
                .andExpect(jsonPath("$.data.writer").value("관리자"))
                .andExpect(jsonPath("$.data.writerEmail").value("admin@example.com"))
                .andExpect(jsonPath("$.data.createAt").value("2025-05-18T10:30:00"))
                .andExpect(jsonPath("$.data.updatedAt").value("2025-05-18T11:00:00"))
                .andExpect(jsonPath("$.data.content").value("공지사항 본문"))
                .andExpect(jsonPath("$.data.fileList[0].fileId").value(10))
                .andExpect(jsonPath("$.data.fileList[0].fileName").value("example.pdf"));
    }

}