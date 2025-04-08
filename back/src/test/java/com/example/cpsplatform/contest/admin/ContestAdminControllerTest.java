package com.example.cpsplatform.contest.admin;

import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.admin.aop.AdminLogProxy;
import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.contest.admin.request.CreateContestRequest;
import com.example.cpsplatform.contest.admin.request.UpdateContestRequest;
import com.example.cpsplatform.contest.admin.service.ContestAdminService;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.service.LoginFailService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityConfig.class, AdminLogProxy.class})
@WebMvcTest(controllers = ContestAdminController.class)
class ContestAdminControllerTest {

    @MockitoBean
    AuthService authService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MemberRepository memberRepository;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @MockitoBean
    LoginFailService loginFailService;

    @MockitoBean
    ContestAdminService contestAdminService;

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회 생성 요청 성공 테스트")
    @Test
    void createContestSuccess() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        CreateContestRequest request = new CreateContestRequest(
                "테스트 대회",
                1,
                "테스트 대회 설명",
                now.plusDays(1),
                now.plusDays(2),
                now.plusDays(3),
                now.plusDays(4)
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 생성할 때, 대회 제목이 비어있는 경우 예외를 반환한다.")
    @Test
    void createContestFailWithEmptyTitle() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        CreateContestRequest request = new CreateContestRequest(
                "",
                1,
                "테스트 대회 설명",
                now.plusDays(1),
                now.plusDays(2),
                now.plusDays(3),
                now.plusDays(4)
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("대회 제목은 필수입니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 생성할 때, 시즌이 0 이하인 경우 예외를 반환한다.")
    @Test
    void createContestFailWithInvalidSeason() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        CreateContestRequest request = new CreateContestRequest(
                "테스트 대회",
                0,
                "테스트 대회 설명",
                now.plusDays(1),
                now.plusDays(2),
                now.plusDays(3),
                now.plusDays(4)
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("대회 연회는 양수여야 합니다."));
    }


    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 생성할 때, 날짜가 null인 경우 예외를 반환한다.")
    @Test
    void createContestFailWithNullDate() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        CreateContestRequest request = new CreateContestRequest(
                "테스트 대회",
                1,
                "테스트 대회 설명",
                null,
                now.plusDays(2),
                now.plusDays(3),
                now.plusDays(4)
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("예선 접수 시작 시간은 필수입니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 생성할 때, 접수 날짜가 과거인 경우 예외를 반환한다.")
    @Test
    void createContestFailWithPastDate() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        CreateContestRequest request = new CreateContestRequest(
                "테스트 대회",
                1,
                "테스트 대회 설명",
                now.minusDays(2),
                now.minusDays(1), // 과거 날짜
                now.plusDays(3),
                now.plusDays(4)
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("예선 접수 마감 시간은 미래 날짜여야 합니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 생성할 때, 접수 시작이 접수 마감보다 늦은 경우 예외를 반환한다.")
    @Test
    void createContestFailWithInvalidDateOrder1() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        CreateContestRequest request = new CreateContestRequest(
                "테스트 대회",
                1,
                "테스트 대회 설명",
                now.plusDays(3), // 접수 시작이 마감보다 늦음
                now.plusDays(2),
                now.plusDays(4),
                now.plusDays(5)
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("예선 접수 시작 시간은 마감 시간보다 이전이어야 합니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 생성할 때, 대회 시작이 대회 종료보다 늦은 경우 예외를 반환한다.")
    @Test
    void createContestFailWithInvalidDateOrder2() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        CreateContestRequest request = new CreateContestRequest(
                "테스트 대회",
                1,
                "테스트 대회 설명",
                now.plusDays(1),
                now.plusDays(2),
                now.plusDays(5), // 대회 시작이 종료보다 늦음
                now.plusDays(4)
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("대회 시작 시간은 종료 시간보다 이전이어야 합니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 수정할 때, 대회 시작이 대회 종료보다 늦은 경우 예외를 반환한다.")
    @Test
    void updateContestFailWithInvalidDateOrder() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        UpdateContestRequest request = new UpdateContestRequest(
                1L,
                "테스트 대회 수정",
                2,
                "테스트 대회 설명 수정",
                now.plusDays(1),
                now.plusDays(2),
                now.plusDays(5), // 대회 시작이 종료보다 늦음
                now.plusDays(4)
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("대회 시작 시간은 종료 시간보다 이전이어야 합니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 수정할 때, 등록 시작이 등록 종료보다 늦은 경우 예외를 반환한다.")
    @Test
    void updateContestFailWithInvalidRegistrationDateOrder() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        UpdateContestRequest request = new UpdateContestRequest(
                1L,
                "테스트 대회 수정",
                2,
                "테스트 대회 설명 수정",
                now.plusDays(3), // 등록 시작이 종료보다 늦음
                now.plusDays(2),
                now.plusDays(4),
                now.plusDays(5)
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("예선 접수 시작 시간은 마감 시간보다 이전이어야 합니다."));
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 수정할 때, 필수 값이 누락된 경우 예외를 반환한다.")
    @Test
    void updateContestFailWithMissingRequiredFields() throws Exception {
        // given
        UpdateContestRequest request = new UpdateContestRequest(
                1L,
                "", // 빈 title
                2,
                "테스트 대회 설명 수정",
                null, // null registrationStartAt
                null, // null registrationEndAt - 필수값 누락
                null, // null contestStartAt - 필수값 누락
                null  // null contestEndAt - 필수값 누락
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 수정 요청을 받아 수정 후 성공 응답을 반환한다.")
    @Test
    void updateContestSuccess() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        UpdateContestRequest request = new UpdateContestRequest(
                1L,
                "테스트 대회 수정",
                2,
                "테스트 대회 설명 수정",
                now.plusDays(1),
                now.plusDays(2),
                now.plusDays(3),
                now.plusDays(4)
        );

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}