package com.example.cpsplatform.teamsolve.repository;

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
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListDto;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListResponse;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamSolveRepositoryCustomImplTest {

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


    @DisplayName("관리자) 팀에서 작성한 답안지를 유형이 null이면 모든 유형의 답안지를 조회한다.")
    @Test
    void findTeamSolveByAdminCond(){
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
        Problem problem2 = Problem.builder()
                .title("문제 제목2")
                .contest(contest)
                .section(Section.COMMON)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();
        problemRepository.saveAll(List.of(problem1,problem2));

        TeamSolve teamSolve1 = TeamSolve.builder()
                .team(team)
                .problem(problem1)
                .teamSolveType(TeamSolveType.TEMP)
                .build();
        TeamSolve teamSolve2 = TeamSolve.builder()
                .team(team)
                .problem(problem2)
                .teamSolveType(TeamSolveType.TEMP)
                .build();
        teamSolveRepository.saveAll(List.of(teamSolve1,teamSolve2));

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

        File file2 = File.builder()
                .name("file.pdf")
                .originalName("file.pdf")
                .extension(FileExtension.PDF)
                .mimeType(FileExtension.PDF.getMimeType())
                .teamSolve(teamSolve2)
                .fileType(FileType.TEAM_SOLUTION)
                .size(100L)
                .path("/path")
                .build();

        fileRepository.saveAll(List.of(file1,file2));

        //when
        TeamSolveListResponse result = teamSolveRepository.findTeamSolveByAdminCond(team.getId(), null);

        //then
        assertThat(result).isNotNull();
        List<TeamSolveListDto> dtoList = result.getTeamSolveListDtos();
        assertThat(dtoList).hasSize(2);

        TeamSolveListDto dto1 = dtoList.get(0);
        TeamSolveListDto dto2 = dtoList.get(1);

        assertThat(dto1.getProblemId()).isEqualTo(problem2.getId());
        assertThat(dto1.getProblemName()).isEqualTo(problem2.getTitle());
        assertThat(dto1.getSection()).isEqualTo(problem2.getSection());
        assertThat(dto1.getType()).isEqualTo(teamSolve2.getTeamSolveType());
        assertThat(dto1.getTeamSolveId()).isEqualTo(teamSolve2.getId());
        assertThat(dto1.getUpdatedAt()).isNotNull();

        assertThat(dto2.getProblemId()).isEqualTo(problem1.getId());
        assertThat(dto2.getProblemName()).isEqualTo(problem1.getTitle());
        assertThat(dto2.getSection()).isEqualTo(problem1.getSection());
        assertThat(dto2.getType()).isEqualTo(teamSolve1.getTeamSolveType());
        assertThat(dto2.getTeamSolveId()).isEqualTo(teamSolve1.getId());
        assertThat(dto2.getUpdatedAt()).isNotNull();

    }

    @DisplayName("관리자) 팀에서 작성한 답안지를 유형이 null이면 모든 유형의 답안지를 조회한다.")
    @Test
    void findTeamSolveByAdminCondWithExactlyType(){
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
                .finalSubmitCount(0)
                .build();
        teamRepository.save(team);

        Problem problem1 = Problem.builder()
                .title("문제 제목")
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
        TeamSolve teamSolve2 = TeamSolve.builder()
                .team(team)
                .problem(problem1)
                .teamSolveType(TeamSolveType.SUBMITTED)
                .build();
        teamSolveRepository.saveAll(List.of(teamSolve1,teamSolve2));

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

        File file2 = File.builder()
                .name("file.pdf")
                .originalName("file.pdf")
                .extension(FileExtension.PDF)
                .mimeType(FileExtension.PDF.getMimeType())
                .teamSolve(teamSolve2)
                .fileType(FileType.TEAM_SOLUTION)
                .size(100L)
                .path("/path")
                .build();

        fileRepository.saveAll(List.of(file1,file2));

        //when
        TeamSolveListResponse result = teamSolveRepository.findTeamSolveByAdminCond(team.getId(), TeamSolveType.TEMP);

        //then
        assertThat(result).isNotNull();
        List<TeamSolveListDto> dtoList = result.getTeamSolveListDtos();
        assertThat(dtoList).hasSize(1);

        TeamSolveListDto dto1 = dtoList.get(0);

        assertThat(dto1.getProblemId()).isEqualTo(problem1.getId());
        assertThat(dto1.getProblemName()).isEqualTo(problem1.getTitle());
        assertThat(dto1.getSection()).isEqualTo(problem1.getSection());
        assertThat(dto1.getType()).isEqualTo(teamSolve1.getTeamSolveType());
        assertThat(dto1.getTeamSolveId()).isEqualTo(teamSolve1.getId());
        assertThat(dto1.getUpdatedAt()).isNotNull();

    }

}