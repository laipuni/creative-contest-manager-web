package com.example.cpsplatform.exception.controller;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.admin.request.CreateContestRequest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.controller.dto.UniqueConstraintMessage;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.team.controller.request.CreateTeamRequest;
import com.example.cpsplatform.team.controller.request.UpdateTeamRequest;
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamnumber.domain.TeamNumber;
import com.example.cpsplatform.teamnumber.repository.TeamNumberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.example.cpsplatform.exception.controller.dto.UniqueConstraintMessage.CONTEST_SEASON;
import static com.example.cpsplatform.exception.controller.dto.UniqueConstraintMessage.TEAM_NAME;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GlobalExceptionHandlerTest {

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    TeamNumberRepository teamNumberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("중복된 시즌의 대회를 저장할 때, 유니크 제약 위반으로 예외가 발생한다.")
    @Test
    void CONTEST_SEASON() throws Exception {
        //given
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("테스트 대회 설명")
                .season(16)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();
        contestRepository.save(contest);

        entityManager.flush();
        entityManager.clear();

        LocalDateTime now = LocalDateTime.now();
        CreateContestRequest request = new CreateContestRequest(
                "테스트 대회",
                16,
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
                        post("/api/admin/contests")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(CONTEST_SEASON.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("중복된 팀의 이름으로 팀을 생성할 때, 유니크 제약 위반으로 예외가 발생한다.")
    @Test
    void DuplicateTeamNameByCreatingTeam() throws Exception {
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        String email = "register@email.com";
        Member member = Member.builder()
                .loginId("member")
                .password("1234")
                .role(Role.USER)
                .birth(LocalDate.now())
                .email(email)
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("사람 이름")
                .organization(school)
                .build();

        Address anotherAddress = new Address("street","city","zipCode","detail");
        School anotherSchool = new School("xx대학교", StudentType.COLLEGE,4);
        String anotherEmail = "another@email.com";
        Member another = Member.builder()
                .loginId("another")
                .password("1234")
                .role(Role.USER)
                .birth(LocalDate.now())
                .email(anotherEmail)
                .address(anotherAddress)
                .gender(Gender.MAN)
                .phoneNumber("01011112222")
                .name("사람 이름")
                .organization(anotherSchool)
                .build();

        Address teammateAddress = new Address("street","city","zipCode","detail");
        School teammateSchool = new School("xx대학교", StudentType.COLLEGE,4);
        String teammateEmail = "teammate@email.com";
        Member teammate = Member.builder()
                .loginId("teammate")
                .password("1234")
                .role(Role.USER)
                .birth(LocalDate.now())
                .email(teammateEmail)
                .address(teammateAddress)
                .gender(Gender.MAN)
                .phoneNumber("01033334444")
                .name("사람 이름")
                .organization(teammateSchool)
                .build();
        memberRepository.saveAll(List.of(member,another,teammate));

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("테스트 대회 설명")
                .season(16)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().plusDays(1))
                .startTime(now().plusDays(2))
                .endTime(now().plusDays(3))
                .build();
        contestRepository.save(contest);

        TeamNumber teamNumber = TeamNumber.builder()
                .contest(contest)
                .lastTeamNumber(1)
                .build();

        teamNumberRepository.save(teamNumber);

        String teamName = "A팀";
        Team team = Team.builder()
                .contest(contest)
                .teamNumber("001")
                .name(teamName)
                .leader(member)
                .section(Section.ELEMENTARY_MIDDLE)
                .winner(false)
                .status(SubmitStatus.NOT_SUBMITTED)
                .finalSubmitCount(0)
                .build();

        teamRepository.save(team);


        entityManager.flush();
        entityManager.clear();

        //팀 생성 요청 로그인 정보 세팅
        SecurityMember securityMember = new SecurityMember(another);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                securityMember, null, securityMember.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        //팀생성 dto 생성
        CreateTeamRequest request = new CreateTeamRequest(
                teamName,contest.getId(),List.of(teammate.getLoginId())
        );

        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        post("/api/teams")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(TEAM_NAME.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("중복된 팀의 이름으로 팀을 생성할 때, 유니크 제약 위반으로 예외가 발생한다.")
    @Test
    void DuplicateTeamNameByUpdatingTeam() throws Exception {
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        String email = "register@email.com";
        Member member = Member.builder()
                .loginId("member")
                .password("1234")
                .role(Role.USER)
                .birth(LocalDate.now())
                .email(email)
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("사람 이름")
                .organization(school)
                .build();

        Address anotherAddress = new Address("street","city","zipCode","detail");
        School anotherSchool = new School("xx대학교", StudentType.COLLEGE,4);
        String anotherEmail = "another@email.com";
        Member another = Member.builder()
                .loginId("another")
                .password("1234")
                .role(Role.USER)
                .birth(LocalDate.now())
                .email(anotherEmail)
                .address(anotherAddress)
                .gender(Gender.MAN)
                .phoneNumber("01011112222")
                .name("사람 이름")
                .organization(anotherSchool)
                .build();

        memberRepository.saveAll(List.of(member,another));

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("테스트 대회 설명")
                .season(16)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().plusDays(1))
                .startTime(now().plusDays(2))
                .endTime(now().plusDays(3))
                .build();
        contestRepository.save(contest);

        String team1Name = "A팀";
        Team team1 = Team.builder()
                .contest(contest)
                .teamNumber("001")
                .name(team1Name)
                .leader(member)
                .section(Section.ELEMENTARY_MIDDLE)
                .winner(false)
                .status(SubmitStatus.NOT_SUBMITTED)
                .finalSubmitCount(0)
                .build();

        String team2Name = "B팀";
        Team team2 = Team.builder()
                .contest(contest)
                .teamNumber("002")
                .name(team2Name)
                .leader(another)
                .section(Section.ELEMENTARY_MIDDLE)
                .winner(false)
                .status(SubmitStatus.NOT_SUBMITTED)
                .finalSubmitCount(0)
                .build();

        teamRepository.saveAll(List.of(team1,team2));

        entityManager.flush();
        entityManager.clear();

        //팀 생성 요청 로그인 정보 세팅
        SecurityMember securityMember = new SecurityMember(another);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                securityMember, null, securityMember.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        //팀 수정 dto 생성
        UpdateTeamRequest request = new UpdateTeamRequest(
                team1Name, Collections.emptyList(), contest.getId()
        );

        String content = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(
                        patch("/api/teams/{teamId}",team2.getId())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(TEAM_NAME.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    //todo 추후에 모든 유니크 제약 테스트 작성

}