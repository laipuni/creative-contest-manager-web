package com.example.cpsplatform.file.repository;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.repository.dto.FileNameDto;
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
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
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
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class FileRepositoryCustomImplTest {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamSolveRepository teamSolveRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("파일 ID 목록으로 파일 이름 DTO를 조회한다")
    @Test
    void findFileNameDto(){
        //given
        Contest contest = Contest.builder()
                .title("테스트 대회 2")
                .description("테스트 대회 설명 2")
                .season(17)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();

        contestRepository.save(contest);

        Problem commonProblem = Problem.builder()
                .title("테스트 공통 문제")
                .contest(contest)
                .section(Section.COMMON)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();

        Problem highNormalProblem = Problem.builder()
                .title("테스트 고등-일반 문제 제목")
                .contest(contest)
                .section(Section.HIGH_NORMAL)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();

        problemRepository.saveAll(List.of(commonProblem, highNormalProblem));

        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
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

        Team team = Team.builder().name("xx팀").winner(false).leader(member).contest(contest).build();
        teamRepository.save(team);

        TeamSolve teamSolve = TeamSolve.builder().team(team).problem(highNormalProblem).build();
        teamSolveRepository.save(teamSolve);

        File file1 = File.builder()
                .problem(highNormalProblem)
                .name("문제1_1.pdf")
                .originalName("문제1_1.pdf")
                .fileType(FileType.PROBLEM_REAL)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(100L)
                .teamSolve(teamSolve)
                .path("/contest/16회/일반/1번")
                .build();

        File file2 = File.builder()
                .problem(commonProblem)
                .name("문제2_1.jpg")
                .originalName("문제2_1.jpg")
                .fileType(FileType.PROBLEM_REAL)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(200L)
                .teamSolve(teamSolve)
                .path("/contest/16회/일반/2번")
                .build();

        fileRepository.saveAll(List.of(file1, file2));

        //when
        List<FileNameDto> fileNameDtos = fileRepository.findFileNameDto(List.of(file1.getId(), file2.getId()));

        //then
        assertThat(fileNameDtos)
                .extracting("fileId", "fileExtension", "section", "teamName", "season", "problemOrder")
                .containsExactlyInAnyOrder(
                        tuple(file1.getId(), FileExtension.PDF, Section.HIGH_NORMAL, "xx팀", 17, 1),
                        tuple(file2.getId(), FileExtension.PDF, Section.COMMON, "xx팀", 17, 1)
                );
    }

}