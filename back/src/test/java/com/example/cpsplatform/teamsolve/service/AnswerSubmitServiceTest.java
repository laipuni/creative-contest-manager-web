package com.example.cpsplatform.teamsolve.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.ContestJoinException;
import com.example.cpsplatform.exception.TemporaryAnswerNotFoundException;
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
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerResponse;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import com.example.cpsplatform.teamsolve.service.dto.FinalSubmitAnswerDto;
import com.example.cpsplatform.teamsolve.service.dto.SubmitAnswerDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.cpsplatform.team.domain.SubmitStatus.NOT_SUBMITTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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
                .loginId("AnswerSubmit")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("AnswerSubmit@email.com")
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
                .season(100)
                .registrationStartAt(now.minusDays(3))
                .registrationEndAt(now.minusDays(1))
                .startTime(now.minusHours(1))
                .endTime(now.plusHours(1))
                .build();
        contestRepository.save(contest);

        team = Team.builder()
                .contest(contest)
                .status(NOT_SUBMITTED)
                .finalSubmitCount(0)
                .teamNumber("001")
                .leader(member)
                .winner(false)
                .name("팀 이름")
                .section(Section.HIGH_NORMAL)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam = MemberTeam.builder()
                .member(member)
                .team(team)
                .build();
        memberTeamRepository.save(memberTeam);

        problem = Problem.builder()
                .title("테스트 문제 제목")
                .contest(contest)
                .section(Section.HIGH_NORMAL)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("테스트 문제 설명")
                .build();
        problemRepository.save(problem);

        teamSolve = TeamSolve.builder()
                .team(team)
                .problem(problem)
                .teamSolveType(TeamSolveType.TEMP)
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
    void submitAnswerTemporary(){
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
        String content = "수정된 내용";
        SubmitAnswerDto answerDto = new SubmitAnswerDto(now, member.getLoginId(), contest.getId(), problem.getId(), content);

        //when
        //then
        assertDoesNotThrow(() -> answerSubmitService.submitAnswerTemporary(fileSource,answerDto));

        List<TeamSolve> solveList = teamSolveRepository.findAll();
        assertThat(solveList).hasSize(1);
        assertThat(solveList.get(0).getContent()).isEqualTo(content);

        //이전파일 삭제
        assertThat(fileRepository.findById(file.getId())).isEmpty();
        assertThat(fileRepository.findAll()).hasSize(1);
        assertThat(fileRepository.findAll().get(0))
                .extracting("name","originalName","extension","mimeType","size")
                .containsExactlyInAnyOrder(
                        fileSource.getUploadFileName(),
                        originalFilename1,
                        FileExtension.PDF,
                        FileExtension.PDF.getMimeType(),
                        fileSource.getSize()
                );
    }

    @DisplayName("팀장이 대회 문제들의 답안지를 제출한다.")
    @Test
    void submitAnswerTemporaryWithFirst(){
        //given
        //임시 저장된 답안 삭제
        fileRepository.hardDeleteAllByTeamSolveIdIn(List.of(teamSolve.getId()));
        teamSolveRepository.delete(teamSolve);

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
        assertDoesNotThrow(() -> answerSubmitService.submitAnswerTemporary(fileSource,answerDto));

        List<TeamSolve> solveList = teamSolveRepository.findAll();
        assertThat(solveList).hasSize(1);

        //파일을 덮어쓴지 확인
        assertThat(fileRepository.findAll()).hasSize(1);
        assertThat(fileRepository.findAll().get(0))
                .extracting("name","originalName","extension","mimeType","size")
                .containsExactlyInAnyOrder(
                        fileSource.getUploadFileName(),
                        originalFilename1,
                        FileExtension.PDF,
                        FileExtension.PDF.getMimeType(),
                        fileSource.getSize()
                );
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
        assertThatThrownBy(() -> answerSubmitService.submitAnswerTemporary(fileSource,answerDto))
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
        assertThatThrownBy(() -> answerSubmitService.submitAnswerTemporary(fileSource,answerDto))
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
        assertThatThrownBy(() -> answerSubmitService.submitAnswerTemporary(fileSource,answerDto))
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
        assertThatThrownBy(() -> answerSubmitService.submitAnswerTemporary(fileSource,answerDto))
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
        GetTeamAnswerResponse response = answerSubmitService.getAnswer(contestId, loginId, null);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTeamAnswerList()).hasSize(1);
        assertThat(response.getTeamAnswerList().get(0))
                .extracting("teamSolveId","teamName","section","fileId","fileName")
                .containsExactly(teamSolve.getId(),team.getName(),problem.getSection(),file.getId(),file.getOriginalName());
    }

    @DisplayName("대회에 참여한 팀이 없으면 예외가 발생한다")
    @Test
    void getAnswerWithNoTeam() {
        // given
        Long contestId = 999L; //존재하지 않는 대회 id
        String loginId = member.getLoginId();

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.getAnswer(contestId, loginId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 대회에 참여한 팀이 없습니다.");
    }

    @DisplayName("임시 저장된 답안지를 최종 답안지로 제출한다.")
    @Test
    void submitAnswerComplete() {
        //given
        //기존 최종 제출 답안 생성
        TeamSolve finalTeamSolve = TeamSolve.builder()
                .team(team)
                .problem(problem)
                .teamSolveType(TeamSolveType.SUBMITTED) //최종 제출 상태
                .build();
        teamSolveRepository.save(finalTeamSolve);

        File finalFile = File.builder()
                .problem(problem)
                .name("최종제출_문제1.pdf")
                .originalName("최종제출_문제1.pdf")
                .fileType(FileType.TEAM_SOLUTION)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(200L)
                .path("final_path")
                .teamSolve(finalTeamSolve)
                .build();
        fileRepository.save(finalFile);

        entityManager.flush();
        entityManager.clear();

        //임시 답안은 이미 BeforeEach에서 생성됨 (teamSolve)
        FinalSubmitAnswerDto answerDto = new FinalSubmitAnswerDto(
                now,
                member.getLoginId(),
                contest.getId()
        );


        //when
        answerSubmitService.submitAnswerComplete(answerDto);

        entityManager.flush();
        entityManager.clear();

        //then
        //임시 답안이 최종 답안으로 변경되었는지 확인
        List<TeamSolve> submittedSolves = teamSolveRepository.findAllByTeamIdAndContestIdAndTeamSolveType(
                team.getId(), contest.getId(), TeamSolveType.SUBMITTED);

        assertThat(submittedSolves).hasSize(1);
        assertThat(submittedSolves.get(0).getId()).isEqualTo(teamSolve.getId());
        assertThat(submittedSolves.get(0).getTeamSolveType()).isEqualTo(TeamSolveType.SUBMITTED);

        //기존 최종 답안이 삭제되었는지 확인
        assertThat(teamSolveRepository.findById(finalTeamSolve.getId())).isEmpty();
        assertThat(fileRepository.findById(finalFile.getId())).isEmpty();

        //팀의 최종 제출 상태 확인
        Team updatedTeam = teamRepository.findById(team.getId()).orElseThrow();
        assertThat(updatedTeam.getStatus()).isEqualTo(SubmitStatus.FINAL);
        assertThat(updatedTeam.getFinalSubmitCount()).isEqualTo(0);
    }

    @DisplayName("최종 제출 시 임시 저장된 답안이 없으면 예외가 발생한다.")
    @Test
    void submitAnswerCompleteWithNoTempAnswer() {
        //given
        //임시 저장된 답안 삭제
        fileRepository.hardDeleteAllByTeamSolveIdIn(List.of(teamSolve.getId()));
        teamSolveRepository.delete(teamSolve);

        FinalSubmitAnswerDto answerDto = new FinalSubmitAnswerDto(
                now,
                member.getLoginId(),
                contest.getId()
        );

        entityManager.flush();
        entityManager.clear();

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswerComplete(answerDto))
                .isInstanceOf(TemporaryAnswerNotFoundException.class)
                .hasMessageContaining(  "최종 제출할 임시 저장한 답안이 없습니다.");
    }

    @DisplayName("대회 개최 기간이 아닌 시간에 최종 답안을 제출하면 예외가 발생한다.")
    @Test
    void submitAnswerCompleteWithNotOnGoingContest() {
        //given
        FinalSubmitAnswerDto answerDto = new FinalSubmitAnswerDto(
                now.minusDays(5), // 대회 시작 5일 전으로 설정
                member.getLoginId(),
                contest.getId()
        );

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswerComplete(answerDto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageContaining("현재 대회시간이 아니라 답을 제출할 수 없습니다");
    }

    @DisplayName("팀장이 아닌 사용자가 최종 답안을 제출하면 예외가 발생한다.")
    @Test
    void submitAnswerCompleteWithNotLeader() {
        //given
        //다른 사용자 생성
        Member otherMember = Member.builder()
                .loginId("otherLoginId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("other@email.com")
                .address(new Address("street", "city", "zipCode", "detail"))
                .gender(Gender.MAN)
                .phoneNumber("01099998888")
                .name("다른사용자")
                .organization(new School("xx대학교", StudentType.COLLEGE, 4))
                .build();
        memberRepository.save(otherMember);

        //팀에 일반 멤버로 추가
        MemberTeam otherMemberTeam = MemberTeam.builder()
                .member(otherMember)
                .team(team)
                .build();
        memberTeamRepository.save(otherMemberTeam);

        FinalSubmitAnswerDto answerDto = new FinalSubmitAnswerDto(
                now,
                otherMember.getLoginId(), // 팀장이 아닌 사용자 ID
                contest.getId()
        );

        entityManager.flush();
        entityManager.clear();

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswerComplete(answerDto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageContaining("답안지는 팀장만이 제출할 수 있습니다");
    }

    @DisplayName("존재하지 않는 대회에 최종 답안을 제출하면 예외가 발생한다.")
    @Test
    void submitAnswerCompleteWithNotExistContest() {
        //given
        Long invalidContestId = 9999L;

        FinalSubmitAnswerDto answerDto = new FinalSubmitAnswerDto(
                now,
                member.getLoginId(),
                invalidContestId
        );

        //when
        //then
        assertThatThrownBy(() -> answerSubmitService.submitAnswerComplete(answerDto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageContaining("답을 제출할 대회가 존재하지 않습니다");
    }
}