package com.example.cpsplatform.contest.admin;

import com.example.cpsplatform.admin.aop.AdminLogProxy;
import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.contest.admin.controller.ContestAdminController;
import com.example.cpsplatform.contest.admin.controller.request.HardDeleteContestRequest;
import com.example.cpsplatform.contest.admin.controller.response.*;
import com.example.cpsplatform.contest.admin.request.CreateContestRequest;
import com.example.cpsplatform.contest.admin.request.DeleteContestRequest;
import com.example.cpsplatform.contest.admin.request.UpdateContestRequest;
import com.example.cpsplatform.contest.admin.request.WinnerTeamsRequest;
import com.example.cpsplatform.contest.admin.service.ContestAdminService;
import com.example.cpsplatform.contest.admin.service.ContestDeleteService;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import java.util.Collections;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockitoBean
    ContestDeleteService contestDeleteService;

    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 대회 목록을 조회하면 해당 페이지의 대회 목록이 반환된다")
    @Test
    void searchContestList() throws Exception {
        // given
        int page = 0;
        List<ContestListDto> contestList = List.of(
                new ContestListDto(1L, "테스트 대회 1", 16,
                        LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(1),
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                new ContestListDto(2L, "테스트 대회 2", 17,
                        LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(1),
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1))
        );

        int totalPage = 3;
        int firstPage = 0;
        int lastPage = 2;
        int size = 2;
        ContestListResponse response = ContestListResponse.builder()
                .page(page)
                .totalPage(totalPage)
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size(size)
                .problemList(contestList)
                .build();

        Mockito.when(contestAdminService.searchContestList(page))
                        .thenReturn(response);

        //when
        //then
        mockMvc.perform(get("/api/admin/contests")
                        .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.totalPage").value(totalPage))
                .andExpect(jsonPath("$.data.firstPage").value(firstPage))
                .andExpect(jsonPath("$.data.lastPage").value(lastPage))
                .andExpect(jsonPath("$.data.size").value(size))
                .andExpect(jsonPath("$.data.problemList").isArray())
                .andExpect(jsonPath("$.data.problemList[0].contestId").value(1L))
                .andExpect(jsonPath("$.data.problemList[0].title").value("테스트 대회 1"))
                .andExpect(jsonPath("$.data.problemList[0].season").value(16))
                .andExpect(jsonPath("$.data.problemList[1].contestId").value(2L))
                .andExpect(jsonPath("$.data.problemList[1].title").value("테스트 대회 2"))
                .andDo(print());
    }

    @WithMockUser(roles = "USER")
    @DisplayName("ADMIN 권한이 없는 사용자는 접근할 수 없다")
    @Test
    void searchContestListUnauthorized() throws Exception {
        //when
        //then
        mockMvc.perform(get("/api/admin/contests"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 특정 대회의 상세 정보를 조회할 수 있다")
    @Test
    void findContestDetail() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        String title = "16회 창의력 경진 대회";
        String description = "대회 설명입니다";
        int season = 16;
        ContestDetailResponse response = ContestDetailResponse.builder()
                .contestId(1L)
                .title(title)
                .description(description)
                .season(season)
                .registrationStartAt(now.minusDays(5))
                .registrationEndAt(now.minusDays(1))
                .startTime(now)
                .endTime(now.plusDays(1))
                .createdAt(now.minusDays(10))
                .updatedAt(now.minusDays(7))
                .build();
        Long contestId = 1L;
        Mockito.when(contestAdminService.findContestDetail(contestId)).thenReturn(response);

        //when
        //then
        mockMvc.perform(get("/api/admin/contests/{contestId}", contestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.contestId").value(1L))
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.description").value(description))
                .andExpect(jsonPath("$.data.season").value(season))
                .andExpect(jsonPath("$.data.registrationStartAt").exists())
                .andExpect(jsonPath("$.data.registrationEndAt").exists())
                .andExpect(jsonPath("$.data.startTime").exists())
                .andExpect(jsonPath("$.data.endTime").exists())
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists())
                .andDo(print());
    }


    @Test
    @DisplayName("인증되지 않은 사용자는 접근할 수 없다")
    void searchContestListUnauthenticated() throws Exception {
        //when
        //then
        mockMvc.perform(get("/api/admin/contests"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회 생성 요청 성공 테스트")
    @Test
    void createContestSuccess() throws Exception {
        //given
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

        //when
        //then
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

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("대회를 삭제요청을 받아 대회를 삭제한다.")
    @Test
    void deleteContestWithNullContestId() throws Exception {
        // given
        DeleteContestRequest request = new DeleteContestRequest(1L);

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/contests")
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
    @DisplayName("대회를 삭제요청을 받았을 때, 삭제할 대회의 정보가 없을 경우 예외가 발생한다.")
    @Test
    void deleteContest() throws Exception {
        // given
        DeleteContestRequest request = new DeleteContestRequest(null);

        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("삭제할 대회의 정보는 필수입니다."));
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 해당 대회에 참여한 팀을 조회하면 해당 페이지의 팀 목록이 반환된다")
    @Test
    void searchTeamListByContest() throws Exception {
        //given
        int page = 0;
        Long contestId = 1L;

        Member leader = Member.builder().loginId("yi").role(Role.USER).build();
        Member leader2 = Member.builder().loginId("kim").role(Role.USER).build();
        List<TeamListByContestDto> teamList = List.of(
                new TeamListByContestDto(1L, "팀1", false, leader.getLoginId(), "002", LocalDateTime.now()),
                new TeamListByContestDto(2L, "팀2", false, leader2.getLoginId(), "003", LocalDateTime.now())
        );

        int totalPage = 3;
        int firstPage = 0;
        int lastPage = 2;
        int size = 2;
        TeamListByContestResponse response = TeamListByContestResponse.builder()
                .page(page)
                .totalPage(totalPage)
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size(size)
                .teamList(teamList)
                .build();

        Mockito.when(contestAdminService.searchTeamListByContest(contestId,page))
                .thenReturn(response);

        //when
        //then
        mockMvc.perform(get("/api/admin/contests/{contestId}/teams", contestId)
                        .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.totalPage").value(totalPage))
                .andExpect(jsonPath("$.data.firstPage").value(firstPage))
                .andExpect(jsonPath("$.data.lastPage").value(lastPage))
                .andExpect(jsonPath("$.data.size").value(size))
                .andExpect(jsonPath("$.data.teamList").isArray())
                .andExpect(jsonPath("$.data.teamList[0].teamId").value(1L))
                .andExpect(jsonPath("$.data.teamList[0].name").value("팀1"))
                .andExpect(jsonPath("$.data.teamList[1].teamId").value(2L))
                .andExpect(jsonPath("$.data.teamList[1].name").value("팀2"))
                .andDo(print());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 최신 대회를 조회하면 최신 대회 정보가 반환된다")
    @Test
    void findLatestContestWithExistContest() throws Exception {
        //given
        ContestLatestResponse response = new ContestLatestResponse(
                3L, 15,"15회 창의력 경진 대회",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        Mockito.when(contestAdminService.findContestLatest())
                .thenReturn(response);

        //when
        //then
        mockMvc.perform(get("/api/admin/contests/latest"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.contestId").value(3L))
                .andExpect(jsonPath("$.data.season").value(15))
                .andExpect(jsonPath("$.data.title").value("15회 창의력 경진 대회"));
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 최신 대회를 조회했지만 대회가 없으면 data는 null로 응답된다")
    @Test
    void findLatestContestWithNoContestExists() throws Exception {
        // given
        Mockito.when(contestAdminService.findContestLatest())
                .thenReturn(null); // 대회 없음

        //when
        //then
        mockMvc.perform(get("/api/admin/contests/latest"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("임시삭제된 대회를 복구하는 요청을 받아 정상적으로 응답한다.")
    @Test
    void recoverContest() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(patch("/api/admin/contests/{contestId}/recover",1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 삭제된 대회 목록을 조회하면 삭제된 대회 목록이 반환된다")
    @Test
    void getDeletedContestList() throws Exception {
        //given
        List<DeletedContestDto> deletedContestList = List.of(
                DeletedContestDto.builder()
                        .contestId(1L)
                        .title("삭제된 테스트 대회 1")
                        .season(16)
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .build(),
                DeletedContestDto.builder()
                        .contestId(2L)
                        .title("삭제된 테스트 대회 2")
                        .season(17)
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .build()
        );

        DeletedContestListResponse response = DeletedContestListResponse.builder()
                .deletedContestList(deletedContestList)
                .build();

        Mockito.when(contestAdminService.findDeletedContest())
                .thenReturn(response);

        //when
        //then
        mockMvc.perform(get("/api/admin/contests/deleted")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.deletedContestList").isArray())
                .andExpect(jsonPath("$.data.deletedContestList.length()").value(2))
                .andExpect(jsonPath("$.data.deletedContestList[0].contestId").value(1L))
                .andExpect(jsonPath("$.data.deletedContestList[0].title").value("삭제된 테스트 대회 1"))
                .andExpect(jsonPath("$.data.deletedContestList[0].season").value(16))
                .andExpect(jsonPath("$.data.deletedContestList[1].contestId").value(2L))
                .andExpect(jsonPath("$.data.deletedContestList[1].title").value("삭제된 테스트 대회 2"))
                .andExpect(jsonPath("$.data.deletedContestList[1].season").value(17))
                .andDo(print());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("대회 완전 삭제 요청을 받아서 정상적으로 삭제하고 응답한다.")
    @Test
    void removeCompletelyContest() throws Exception {
        //given

        HardDeleteContestRequest request = new HardDeleteContestRequest(1L);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(delete("/api/admin/contests/hard")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("대회 완전 삭제 요청을 받아서 정상적으로 삭제하고 응답한다.")
    @Test
    void removeCompletelyContestWithNullContestId() throws Exception {
        //given

        HardDeleteContestRequest request = new HardDeleteContestRequest(null);
        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(delete("/api/admin/contests/hard")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("삭제할 대회의 정보는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }
    @WithMockUser(roles = "ADMIN")
    @DisplayName("합격/불합격 처리한 팀들의 정보를 받아 처리한 뒤 정상적으로 응답한다.")
    @Test
    void toggleWinnerTeams() throws Exception {
        //given
        Long contestId = 1L;
        List<Long> teamIds = List.of(1L,2L);
        WinnerTeamsRequest request = new WinnerTeamsRequest(teamIds);

        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(patch("/api/admin/contests/{contestId}/winners", contestId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("합격/불합격 처리한 팀들의 정보를 받아 처리한 뒤 정상적으로 응답한다.")
    @Test
    void toggleWinnerTeamsWithEmptyTeamIds() throws Exception {
        //given
        Long contestId = 1L;
        List<Long> teamIds = Collections.emptyList();
        WinnerTeamsRequest request = new WinnerTeamsRequest(teamIds);

        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(patch("/api/admin/contests/{contestId}/winners", contestId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("본선에 진출할 팀들의 정보는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}