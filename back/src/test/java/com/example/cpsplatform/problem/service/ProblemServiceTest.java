package com.example.cpsplatform.problem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.contest.service.ContestJoinService;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.problem.controller.response.TeamProblemResponse;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Transactional
class ProblemServiceTest {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private MemberTeamRepository memberTeamRepository;

    @Autowired
    private ContestJoinService contestService;

    @Autowired
    private ContestRepository contestRepository;

    @Test
    @DisplayName("팀의 섹션에 맞는 문제를 불러올 수 있다.(특정섹션문제, 공통문제 총 두개)")
    void getProblemsForTeam() {
        // given
        String loginId = "leaderId";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx초등학교", StudentType.ELEMENTARY, 4);
        Member leader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("팀장")
                .organization(school)
                .build();
        memberRepository.save(leader);

        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(1)
                .registrationStartAt(now.minusDays(5))
                .registrationEndAt(now.minusDays(2))
                .startTime(now.minusHours(1))
                .endTime(now.plusHours(2))
                .build();
        contestRepository.save(contest);

        Team team = Team.builder()
                .name("테스트팀")
                .winner(false)
                .leader(leader)
                .contest(contest)
                .teamNumber("003")
                .section(Section.ELEMENTARY_MIDDLE)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam = MemberTeam.builder()
                .member(leader)
                .team(team)
                .build();

        memberTeamRepository.save(memberTeam);

        Problem problemForElementaryMiddle = Problem.builder()
                .section(Section.ELEMENTARY_MIDDLE)
                .title("초중등용 기출문제")
                .contest(contest)
                .problemType(ProblemType.CONTEST)
                .build();
        Problem problemForCommon = Problem.builder()
                .section(Section.COMMON)
                .title("공통 기출문제")
                .problemType(ProblemType.CONTEST)
                .contest(contest)
                .build();
        problemRepository.save(problemForElementaryMiddle);
        problemRepository.save(problemForCommon);

        // when
        List<TeamProblemResponse> problems = problemService.getProblemsForTeam(team.getId(), contest.getId(), loginId);

        // then
        assertThat(problems).hasSize(2);
        assertThat(problems)
                .extracting("problemType")
                .containsExactlyInAnyOrder(Section.ELEMENTARY_MIDDLE, Section.COMMON);
    }


}