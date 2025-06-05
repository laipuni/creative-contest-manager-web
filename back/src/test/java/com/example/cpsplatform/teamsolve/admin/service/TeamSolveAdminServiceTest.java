package com.example.cpsplatform.teamsolve.admin.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import com.example.cpsplatform.team.domain.Division;
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveDetailResponse;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamSolveAdminServiceTest {

    @Autowired
    TeamSolveRepository teamSolveRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TeamSolveAdminService teamSolveAdminService;

    @DisplayName("팀이 제출한 답안지를 답안지 파일과 함께 상세 조회한다.")
    @Test
    void getTeamSolveDetail(){
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(loginId)
                .password("1234")
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("사람 이름")
                .organization(school)
                .build();

        memberRepository.save(member);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(16)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        Team team = Team.builder().name("팀 이름")
                .winner(false)
                .teamNumber("001")
                .leader(member)
                .contest(contest)
                .status(SubmitStatus.TEMPORARY)
                .division(Division.COLLEGE_GENERAL)
                .finalSubmitCount(0)
                .build();
        teamRepository.save(team);

        Problem problem1 = Problem.builder()
                .title("문제 제목1")
                .contest(contest)
                .section(Section.HIGH_NORMAL)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();
        problemRepository.save(problem1);

        TeamSolve teamSolve1 = TeamSolve.builder()
                .team(team)
                .problem(problem1)
                .teamSolveType(TeamSolveType.TEMP)
                .build();
        teamSolveRepository.save(teamSolve1);

        File file1 = File.builder()
                .name("file.pdf")
                .originalName("file.pdf")
                .extension(FileExtension.PDF)
                .mimeType(FileExtension.PDF.getMimeType())
                .teamSolve(teamSolve1)
                .fileType(FileType.TEAM_SOLUTION)
                .size(100L)
                .path("/path")
                .build();
        fileRepository.save(file1);

        //when
        TeamSolveDetailResponse response = teamSolveAdminService.getTeamSolveDetail(team.getId(), teamSolve1.getId());
        //then
        assertThat(response.getTeamSolveId()).isEqualTo(teamSolve1.getId());
        assertThat(response.getProblemTitle()).isEqualTo(problem1.getTitle());
        assertThat(response.getSection()).isEqualTo(problem1.getSection());
        assertThat(response.getProblemOrder()).isEqualTo(problem1.getProblemOrder());
        assertThat(response.getTeamSolveType()).isEqualTo(teamSolve1.getTeamSolveType());
        assertThat(response.getContent()).isEqualTo(teamSolve1.getContent());
        assertThat(response.getFileId()).isEqualTo(file1.getId());
        assertThat(response.getFileName()).isEqualTo(file1.getOriginalName());
    }

    @DisplayName("팀이 제출한 답안지를 답안지 파일과 함께 상세 조회한다.")
    @Test
    void getTeamSolveDetailWithNotFile(){
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(loginId)
                .password("1234")
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("사람 이름")
                .organization(school)
                .build();

        memberRepository.save(member);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(16)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        Team team = Team.builder().name("팀 이름")
                .winner(false)
                .teamNumber("001")
                .leader(member)
                .contest(contest)
                .status(SubmitStatus.TEMPORARY)
                .division(Division.COLLEGE_GENERAL)
                .finalSubmitCount(0)
                .build();
        teamRepository.save(team);

        Problem problem1 = Problem.builder()
                .title("문제 제목1")
                .contest(contest)
                .section(Section.HIGH_NORMAL)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();
        problemRepository.save(problem1);

        TeamSolve teamSolve1 = TeamSolve.builder()
                .team(team)
                .problem(problem1)
                .teamSolveType(TeamSolveType.TEMP)
                .build();
        teamSolveRepository.save(teamSolve1);

        //when
        TeamSolveDetailResponse response = teamSolveAdminService.getTeamSolveDetail(team.getId(), teamSolve1.getId());
        //then
        assertThat(response.getTeamSolveId()).isEqualTo(teamSolve1.getId());
        assertThat(response.getProblemTitle()).isEqualTo(problem1.getTitle());
        assertThat(response.getSection()).isEqualTo(problem1.getSection());
        assertThat(response.getProblemOrder()).isEqualTo(problem1.getProblemOrder());
        assertThat(response.getTeamSolveType()).isEqualTo(teamSolve1.getTeamSolveType());
        assertThat(response.getContent()).isEqualTo(teamSolve1.getContent());
        assertThat(response.getFileId()).isNull();
        assertThat(response.getFileName()).isNull();
    }
}