package com.example.cpsplatform.teamsolve.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.ContestJoinException;
import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.storage.FileStorage;
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
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerDto;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerResponse;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import com.example.cpsplatform.teamsolve.service.dto.SubmitAnswerDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
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

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    MemberTeamRepository memberTeamRepository;

    @MockitoBean
    FileStorage fileStorage;

    FileSources fileSources;

    //해당 테스트에서 사용할 테스트 전역 변수들
    LocalDateTime now;
    Member member;
    Problem problem;
    Contest contest;
    Team team;
    TeamSolve teamSolve;
    File file;

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
                .phoneNumber("01012344321")
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

        team = Team.builder().contest(contest).teamNumber("001").leader(member).winner(false).name("팀 이름").build();
        teamRepository.save(team);

        MemberTeam memberTeam = MemberTeam.builder()
                .member(member)
                .team(team)
                .build();
        memberTeamRepository.save(memberTeam);

        problem = Problem.builder()
                .title("문제 제목")
                .contest(contest)
                .section(Section.HIGH_NORMAL)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();
        problemRepository.save(problem);

        teamSolve = TeamSolve.builder()
                .team(team)
                .problem(problem)
                .build();
        teamSolveRepository.save(teamSolve);

        file = File.builder()
                .problem(problem)
                .name("문제1_1.pdf")
                .originalName("문제1_1.pdf")
                .fileType(FileType.TEAM_SOLUTION)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(100L)
                .path("path")
                .teamSolve(teamSolve)
                .build();

        fileRepository.save(file);

        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("팀장이 대회 문제들의 답안지를 제출한다.")
    @Test
    void submitAnswer(){
        //given
        String originalFilename1 = "문제1_1.pdf";
        FileSource fileSource = new FileSource(
                "upload1.pdf",
                originalFilename1,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );
        SubmitAnswerDto answerDto = new SubmitAnswerDto(now, member.getLoginId(), contest.getId(), problem.getId(), "빈 내용");

        //when
        //then
        assertDoesNotThrow(() -> answerSubmitService.submitAnswer(fileSource,answerDto));
    }

    @DisplayName("대회 개최 기간이 아닌 시간에 답안지를 제출하면 예외가 발생한다.")
    @Test
    void submitAnswerWithNotOnGoingContest(){
        //given
        String originalFilename = "문제1_1.pdf";
        FileSource fileSource = new FileSource(
                "upload1.pdf",
                originalFilename,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        //대회 시작 5일 전으로 대회시간이 아니도록 설정
        SubmitAnswerDto answerDto = new SubmitAnswerDto(now.minusDays(5), member.getLoginId(), contest.getId(), problem.getId(),"content");

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswer(fileSource,answerDto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageMatching("현재 대회시간이 아니라 답을 제출할 수 없습니다.");
    }

    @DisplayName("답안지를 제출할 때 대회가 존재하지 않으면 예외가 발생한다.")
    @Test
    void submitAnswerWithNotExistContest(){
        //given
        String originalFilename1 = "문제1_1.pdf";
        FileSource fileSource = new FileSource(
                "upload1.pdf",
                originalFilename1,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );
        Long invalidContestId = 9999L;

        SubmitAnswerDto answerDto = new SubmitAnswerDto(now, member.getLoginId(), invalidContestId, problem.getId(), "빈 내용");

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswer(fileSource,answerDto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageMatching("답을 제출할 대회가 존재하지 않습니다.");
    }

    @DisplayName("답안지를 제출하는 유저가 해당 대회에 팀장이 아닌 경우 예외가 발생한다.")
    @Test
    void submitAnswerWithNotLeader(){
        //given
        String originalFilename1 = "문제1_1.pdf";
        FileSource fileSource = new FileSource(
                "upload1.pdf",
                originalFilename1,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        String invalidLoginId = "invalidLoginId";
        SubmitAnswerDto answerDto = new SubmitAnswerDto(now, invalidLoginId, contest.getId(), problem.getId(), "빈 내용");

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswer(fileSource,answerDto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageMatching("답안지는 팀장만이 제출할 수 있습니다.");
    }

    @DisplayName("답안지를 제출할 때 문제가 존재하지 않으면 예외가 발생한다.")
    @Test
    void submitAnswerWithNotExistProblem(){
        //given
        String originalFilename1 = "문제1_1.pdf";
        FileSource fileSource = new FileSource(
                "upload1.pdf",
                originalFilename1,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        Long invalidProblemId = 9999L;

        SubmitAnswerDto answerDto = new SubmitAnswerDto(now, member.getLoginId(), contest.getId(), invalidProblemId, "빈 내용");

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswer(fileSource,answerDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("해당 대회 문제는 존재하지 않습니다.");
    }

    @DisplayName("대회에 참여한 팀의 답안지를 성공적으로 조회한다")
    @Test
    void getAnswerSuccess() {
        // given
        Long contestId = contest.getId();
        String loginId = member.getLoginId();

        //when
        GetTeamAnswerResponse response = answerSubmitService.getAnswer(contestId, loginId);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTeamAnswerList()).hasSize(1);
        assertThat(response.getTeamAnswerList().get(0))
                .extracting("teamSolveId","teamName","section","modifyCount","fileId","fileName")
                .containsExactly(teamSolve.getId(),team.getName(),problem.getSection(),teamSolve.getModifyCount(),file.getId(),file.getOriginalName());
    }

    @DisplayName("대회에 참여한 팀이 없으면 예외가 발생한다")
    @Test
    void getAnswerWithNoTeam() {
        // given
        Long contestId = 999L; //존재하지 않는 대회 id
        String loginId = member.getLoginId();

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.getAnswer(contestId, loginId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 대회에 참여한 팀이 없습니다.");
    }


}