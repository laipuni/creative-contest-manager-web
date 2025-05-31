package com.example.cpsplatform.contest.admin.service;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.CertificateRepository;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.finalcontest.FinalContest;
import com.example.cpsplatform.finalcontest.repository.FinalContestRepository;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import com.example.cpsplatform.team.domain.Division;
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamnumber.domain.TeamNumber;
import com.example.cpsplatform.teamnumber.repository.TeamNumberRepository;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ContestDeleteServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamNumberRepository teamNumberRepository;

    @Autowired
    TeamSolveRepository teamSolveRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    MemberTeamRepository memberTeamRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    FinalContestRepository finalContestRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ContestDeleteService contestDeleteService;

    Contest contest;

    @Transactional
    @BeforeEach
    void tearUp(){
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("경북대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId("1234")
                .password("1234")
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("2tgb02023@gmail.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("010xxxxxxxx")
                .name("이진호")
                .organization(school)
                .build();
        memberRepository.save(member);

        //대회 생성
        FinalContest finalContest = FinalContest.builder()
                .title("테스트 본선 대회")
                .location("대한민국")
                .startTime(now().plusDays(10))
                .endTime(now().plusDays(10).plusHours(1))
                .build();

        contest = Contest.builder()
                .title("테스트 대회")
                .description("테스트 대회 설명")
                .season(16)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .deleted(true)
                .finalContest(finalContest)
                .build();

        contestRepository.save(contest);

        //대회 접수 번호 생성
        TeamNumber teamNumber = TeamNumber.builder()
                .lastTeamNumber(2)
                .contest(contest)
                .build();
        teamNumberRepository.save(teamNumber);

        // 문제 3개 생성 (각 섹션별로 1개씩)
        Problem commonProblem = Problem.builder()
                .title("일반 문제")
                .contest(contest)
                .section(Section.COMMON)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("일반 문제 설명")
                .build();

        Problem elementaryMiddleProblem = Problem.builder()
                .title("초등-중등 문제")
                .contest(contest)
                .section(Section.ELEMENTARY_MIDDLE)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("초등-중등 문제 설명")
                .build();

        Problem highNormalProblem = Problem.builder()
                .title("고등-일반 문제")
                .contest(contest)
                .section(Section.HIGH_NORMAL)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("고등-일반 문제 설명")
                .build();
        problemRepository.saveAll(List.of(commonProblem, elementaryMiddleProblem, highNormalProblem));

        Team team = Team.builder()
                .teamNumber("002")
                .name("팀2")
                .winner(false)
                .leader(member)
                .contest(contest)
                .division(Division.COLLEGE_GENERAL)
                .section(Section.ELEMENTARY_MIDDLE)
                .status(SubmitStatus.NOT_SUBMITTED)
                .finalSubmitCount(0)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam = MemberTeam.builder().member(member).team(team).build();
        memberTeamRepository.save(memberTeam);

        TeamSolve teamSolve = TeamSolve.builder().teamSolveType(TeamSolveType.TEMP).team(team).problem(elementaryMiddleProblem).build();
        teamSolveRepository.save(teamSolve);

        String teamSolveFileName = "testFile.pdf";
        File teamSolveFile = File.builder()
                .name(teamSolveFileName)
                .originalName(teamSolveFileName)
                .fileType(FileType.TEAM_SOLUTION)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(300L)
                .teamSolve(teamSolve) //팀 답안지
                .path("answer/16/공통/1번/")
                .build();
        fileRepository.save(teamSolveFile);

        String problemFileName = "testFile.pdf";
        File problemFile = File.builder()
                .problem(elementaryMiddleProblem) //초-중등 문제
                .name(problemFileName)
                .originalName(problemFileName)
                .fileType(FileType.PROBLEM_REAL)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(300L)
                .path("problem/16/공통/1번/")
                .build();
        fileRepository.save(problemFile);

        Certificate certificate = Certificate.builder()
                .serialNumber(UUID.randomUUID().toString())
                .certificateType(CertificateType.PRELIMINARY)
                .contest(contest)
                .team(team)
                .member(member)
                .title("16회 예선 첨여 확인증")
                .build();

        certificateRepository.save(certificate);
    }

    @DisplayName("대회와 관련된 모든 데이터를 삭제한다.")
    @Test
    void deleteCompletelyContest(){
        //given
        //when
        contestDeleteService.deleteCompletelyContest(contest.getId());
        //then
        assertThat(contestRepository.findAll()).isEmpty();
        assertThat(teamRepository.findAll()).isEmpty();
        assertThat(teamNumberRepository.findAll()).isEmpty();
        assertThat(teamSolveRepository.findAll()).isEmpty();
        assertThat(certificateRepository.findAll()).isEmpty();
        assertThat(fileRepository.findAll()).isEmpty();
        assertThat(memberTeamRepository.findAll()).isEmpty();
        assertThat(problemRepository.findAll()).isEmpty();
        assertThat(finalContestRepository.findAll()).isEmpty();
    }

}