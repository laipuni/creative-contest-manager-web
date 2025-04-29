package com.example.cpsplatform.problem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
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
    private ContestRepository contestRepository;

    @Test
    void getProblemsForTeam() {
        // given
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx초등학교", StudentType.ELEMENTARY, 4);
        Member leader = Member.builder()
                .loginId("leaderId")
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

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(2025)
                .registrationStartAt(LocalDateTime.now())
                .registrationEndAt(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(7))
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
        List<TeamProblemResponse> problems = problemService.getProblemsForTeam(team.getId());

        // then
        assertThat(problems).hasSize(2);
        assertThat(problems)
                .extracting("problemType")
                .containsExactlyInAnyOrder(Section.ELEMENTARY_MIDDLE, Section.COMMON);
    }


}