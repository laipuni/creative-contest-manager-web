package com.example.cpsplatform.teamsolve.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.ContestJoinException;
import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.storage.FileStorage;
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
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import com.example.cpsplatform.teamsolve.service.dto.SubmitAnswerDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
@Transactional
@SpringBootTest
class AnswerSubmitServiceTest {

    @Autowired
    AnswerSubmitService answerSubmitService;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    TeamSolveRepository teamSolveRepository;

    @MockitoBean
    FileStorage fileStorage;

    FileSources fileSources;

    LocalDateTime now;
    Member member;
    Problem problem;
    Contest contest;

    @BeforeEach
    void tearUp(){
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        member = Member.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("이름")
                .organization(school)
                .build();
        memberRepository.save(member);

        now = LocalDateTime.now();
        //테스트 시간 - 1 ~ 테스트 시간 ~ 테스트 시간 + 1 으로 대회시간 설정
        contest = Contest.builder()
                .title("테스트 대회")
                .season(16)
                .registrationStartAt(now.minusDays(3))
                .registrationEndAt(now.minusDays(1))
                .startTime(now.minusHours(1))
                .endTime(now.plusHours(1))
                .build();
        contestRepository.save(contest);

        Team team = Team.builder().contest(contest).leader(member).winner(false).name("팀 이름").build();
        teamRepository.save(team);

        problem = Problem.builder()
                .title("문제 제목")
                .contest(contest)
                .section(Section.HIGH_NORMAL)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();
        problemRepository.save(problem);
        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("팀장이 대회 문제들의 답안지를 제출한다.")
    @Test
    void submitAnswer(){
        //given
        String originalFilename1 = "문제1_1.pdf";
        FileSource fileSource1 = new FileSource(
                "upload1.pdf",
                originalFilename1,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        String originalFilename2 = "문제1_2.pdf";
        FileSource fileSource2 = new FileSource(
                "upload2.pdf",
                originalFilename2,
                new byte[]{4, 5, 6},
                "application/pdf",
                FileExtension.PDF,
                200L
        );


        List<FileSource> fileSourceList = List.of(fileSource1, fileSource2);
        FileSources fileSources = FileSources.of(fileSourceList);

        SubmitAnswerDto answerDto = new SubmitAnswerDto(now, member.getLoginId(), contest.getId(), List.of(problem.getId()));

        //when
        //then
        assertDoesNotThrow(() -> answerSubmitService.submitAnswer(fileSources,answerDto));
    }

    @DisplayName("대회 개최 기간이 아닌 시간에 답안지를 제출하면 예외가 발생한다.")
    @Test
    void submitAnswerWithNotOnGoingContest(){
        //given
        String originalFilename1 = "문제1_1.pdf";
        FileSource fileSource1 = new FileSource(
                "upload1.pdf",
                originalFilename1,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        String originalFilename2 = "문제1_2.pdf";
        FileSource fileSource2 = new FileSource(
                "upload2.pdf",
                originalFilename2,
                new byte[]{4, 5, 6},
                "application/pdf",
                FileExtension.PDF,
                200L
        );


        List<FileSource> fileSourceList = List.of(fileSource1, fileSource2);
        FileSources fileSources = FileSources.of(fileSourceList);

        //대회 시작 5일 전으로 대회시간이 아니도록 설정
        SubmitAnswerDto answerDto = new SubmitAnswerDto(now.minusDays(5), member.getLoginId(), contest.getId(), List.of(problem.getId()));

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswer(fileSources,answerDto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageMatching("현재 대회시간이 아니라 답을 제출할 수 없습니다.");
    }

    @DisplayName("답안지를 제출할 때 대회가 존재하지 않으면 예외가 발생한다.")
    @Test
    void submitAnswerWithNotExistContest(){
        //given
        String originalFilename1 = "문제1_1.pdf";
        FileSource fileSource1 = new FileSource(
                "upload1.pdf",
                originalFilename1,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        String originalFilename2 = "문제1_2.pdf";
        FileSource fileSource2 = new FileSource(
                "upload2.pdf",
                originalFilename2,
                new byte[]{4, 5, 6},
                "application/pdf",
                FileExtension.PDF,
                200L
        );

        Long invalidContestId = 9999L;

        List<FileSource> fileSourceList = List.of(fileSource1, fileSource2);
        FileSources fileSources = FileSources.of(fileSourceList);

        SubmitAnswerDto answerDto = new SubmitAnswerDto(now, member.getLoginId(), invalidContestId, List.of(problem.getId()));

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswer(fileSources,answerDto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageMatching("답을 제출할 대회가 존재하지 않습니다.");
    }

    @DisplayName("답안지를 제출하는 유저가 해당 대회에 팀장이 아닌 경우 예외가 발생한다.")
    @Test
    void submitAnswerWithNotLeader(){
        //given
        String originalFilename1 = "문제1_1.pdf";
        FileSource fileSource1 = new FileSource(
                "upload1.pdf",
                originalFilename1,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        String originalFilename2 = "문제1_2.pdf";
        FileSource fileSource2 = new FileSource(
                "upload2.pdf",
                originalFilename2,
                new byte[]{4, 5, 6},
                "application/pdf",
                FileExtension.PDF,
                200L
        );

        String invalidLoginId = "invalidLoginId";
        List<FileSource> fileSourceList = List.of(fileSource1, fileSource2);
        FileSources fileSources = FileSources.of(fileSourceList);

        SubmitAnswerDto answerDto = new SubmitAnswerDto(now, invalidLoginId, contest.getId(), List.of(problem.getId()));

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswer(fileSources,answerDto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageMatching("답안지는 팀장만이 제출할 수 있습니다.");
    }

    @DisplayName("답안지를 제출할 때 문제가 존재하지 않으면 예외가 발생한다.")
    @Test
    void submitAnswerWithNotExistProblem(){
        //given
        String originalFilename1 = "문제1_1.pdf";
        FileSource fileSource1 = new FileSource(
                "upload1.pdf",
                originalFilename1,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        String originalFilename2 = "문제1_2.pdf";
        FileSource fileSource2 = new FileSource(
                "upload2.pdf",
                originalFilename2,
                new byte[]{4, 5, 6},
                "application/pdf",
                FileExtension.PDF,
                200L
        );

        Long invalidProblemId = 9999L;
        List<FileSource> fileSourceList = List.of(fileSource1, fileSource2);
        FileSources fileSources = FileSources.of(fileSourceList);

        SubmitAnswerDto answerDto = new SubmitAnswerDto(now, member.getLoginId(), contest.getId(), List.of(invalidProblemId));

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswer(fileSources,answerDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("해당 대회 문제는 존재하지 않습니다.");
    }
}