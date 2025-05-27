package com.example.cpsplatform.file.repository;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
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
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
@SpringBootTest
class FileRepositoryTest {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TeamSolveRepository teamSolveRepository;

    @Autowired
    TeamRepository teamRepository;



    @DisplayName("문제의 id로 삭제되지 않은 출제용 문제 파일을 복수 조회한다.")
    @Test
    void findAllByProblemIdAndFileTypeAndNotDeleted(){
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

        Problem problem = Problem.builder()
                .title("문제 제목")
                .contest(contest)
                .section(Section.COMMON)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();
        problemRepository.save(problem);

        List<File> files = List.of(
                createFile(problem,"문제1_1.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/1번"),
                createFile(problem,"문제1_2.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/2번"),
                createFile(problem,"문제1_3.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/3번"),
                createFile(problem,"문제1_4.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/4번")
        );

        fileRepository.saveAll(files);

        //when
        List<File> result = fileRepository.findAllByProblemIdAndFileTypeAndNotDeleted(problem.getId(), FileType.PROBLEM_REAL);
        //then

        assertThat(result).hasSize(4)
                .extracting("problem","name","originalName","fileType","mimeType","extension","size","path","deleted")
                .containsExactly(
                        tuple(problem,"문제1_1.pdf","문제1_1.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100L, "/contest/16회/일반/1번",false),
                        tuple(problem,"문제1_2.pdf","문제1_2.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100L, "/contest/16회/일반/2번",false),
                        tuple(problem,"문제1_3.pdf","문제1_3.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100L, "/contest/16회/일반/3번",false),
                        tuple(problem,"문제1_4.pdf","문제1_4.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100L, "/contest/16회/일반/4번",false)
                );
    }

    @DisplayName("문제의 id로 문제 파일을 soft delete 한다.")
    @Test
    void softDeletedByProblemId(){
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

        Problem problem = Problem.builder()
                .title("문제 제목")
                .contest(contest)
                .section(Section.COMMON)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();
        problemRepository.save(problem);

        List<File> files = List.of(
                createFile(problem,"문제1_1.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/1번"),
                createFile(problem,"문제1_2.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/2번"),
                createFile(problem,"문제1_3.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/3번"),
                createFile(problem,"문제1_4.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/4번")
        );

        fileRepository.softDeletedByProblemId(problem.getId());

        //when
        List<File> result = fileRepository.findAllByProblemIdAndFileTypeAndNotDeleted(problem.getId(), FileType.PROBLEM_REAL);
        //then
        assertThat(result).isEmpty();
    }

    @DisplayName("TeamSolve의 id List들로 File을 소프트 딜리트 한다.")
    @Test
    void softDeletedByTeamSolveIdList(){
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("teamsolve@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01045686542")
                .name("이름")
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

        Team team = Team.builder()
                .name("팀 이름")
                .winner(false)
                .teamNumber("001")
                .leader(member)
                .contest(contest)
                .status(SubmitStatus.TEMPORARY)
                .build();
        teamRepository.save(team);

        Problem problem1 = Problem.builder()
                .title("첫번째 문제 제목")
                .contest(contest)
                .section(Section.HIGH_NORMAL)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("첫번째 문제 설명")
                .build();

        Problem problem2 = Problem.builder()
                .title("두번째 문제 제목")
                .contest(contest)
                .section(Section.COMMON)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("두번째 문제 설명")
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
        teamSolveRepository.saveAll(List.of(teamSolve1, teamSolve2));

        File file1 = File.builder()
                .teamSolve(teamSolve1)
                .name("답안지1_1.pdf")
                .originalName("답안지1_1.pdf")
                .fileType(FileType.TEAM_SOLUTION)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(100L)
                .path("/contest/16회/고등-일반/1번")
                .build();

        File file2 = File.builder()
                .teamSolve(teamSolve2)
                .name("답안지1_2.pdf")
                .originalName("답안지1_2.pdf")
                .fileType(FileType.TEAM_SOLUTION)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(200L)
                .path("/contest/16회/공통/1번")
                .build();

        fileRepository.saveAll(List.of(file1,file2));
        //when
        fileRepository.softDeletedByTeamSolveIdList(List.of(teamSolve1.getId(),teamSolve2.getId()));

        //then
        List<File> result = fileRepository.findAll();
        Assertions.assertThat(result).isEmpty();
    }

    @DisplayName("해당 파일이 팀이 제출한 답안지인지 확인한다.")
    @Test
    void existsAnswerFileForTeam(){
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("teamsolve@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01045686542")
                .name("이름")
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

        Team team = Team.builder()
                .name("팀 이름")
                .winner(false)
                .teamNumber("001")
                .leader(member)
                .contest(contest)
                .status(SubmitStatus.TEMPORARY)
                .build();
        teamRepository.save(team);

        Problem problem = Problem.builder()
                .title("첫번째 문제 제목")
                .contest(contest)
                .section(Section.HIGH_NORMAL)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("첫번째 문제 설명")
                .build();

        problemRepository.save(problem);

        TeamSolve teamSolve = TeamSolve.builder()
                .team(team)
                .teamSolveType(TeamSolveType.TEMP)
                .problem(problem)
                .build();
        teamSolveRepository.save(teamSolve);

        //teamsolve를 참조하는 file
        File file = File.builder()
                .teamSolve(teamSolve)
                .name("답안지1_1.pdf")
                .originalName("답안지1_1.pdf")
                .fileType(FileType.TEAM_SOLUTION)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(100L)
                .path("/contest/16회/고등-일반/1번")
                .build();

        fileRepository.save(file);
        //when
        boolean result = fileRepository.existsAnswerFileForTeam(team.getId(), file.getId(), FileType.TEAM_SOLUTION);
        //then
        assertThat(result).isTrue();
    }

    private static File createFile(Problem problem,String name, FileType fileType, String mimeType, FileExtension extension, long size, String path) {
        return File.builder()
                .problem(problem)
                .name(name)
                .originalName(name)
                .fileType(fileType)
                .mimeType(mimeType)
                .extension(extension)
                .size(size)
                .path(path)
                .build();
    }

}