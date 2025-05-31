package com.example.cpsplatform.teamsolve.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.file.decoder.vo.FileSource;
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
import com.example.cpsplatform.notice.domain.Notice;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import com.example.cpsplatform.team.domain.Division;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import com.example.cpsplatform.teamsolve.service.dto.FinalSubmitAnswerDto;
import com.example.cpsplatform.teamsolve.service.dto.SubmitAnswerDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.cpsplatform.team.domain.SubmitStatus.NOT_SUBMITTED;
import static com.example.cpsplatform.team.domain.SubmitStatus.TEMPORARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AnswerSubmitServiceDeadLockTest {

    @MockitoBean
    FileStorage fileStorage;

    @Autowired
    AnswerSubmitService answerSubmitService;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    MemberTeamRepository memberTeamRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    TeamSolveRepository teamSolveRepository;

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
                .password("1234")
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

        team = Team.builder()
                .contest(contest)
                .status(TEMPORARY)
                .finalSubmitCount(0)
                .teamNumber("001")
                .leader(member)
                .winner(false)
                .name("팀 이름")
                .section(Section.HIGH_NORMAL)
                .division(Division.COLLEGE_GENERAL)
                .build();
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

    }


    @DisplayName("쓰레드 100개로 임시 저장 100회 요청을 했을 때, 데드락이 발생하지 않는다.")
    @Test
    void retrieveNotice() throws InterruptedException {
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

        SubmitAnswerDto answerDto = new SubmitAnswerDto(
                now,
                member.getLoginId(),
                contest.getId(),
                problem.getId(),
                "빈 내용"
        );

        FinalSubmitAnswerDto finalAnswerDto = new FinalSubmitAnswerDto(now, member.getLoginId(), contest.getId());


        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    answerSubmitService.submitAnswerTemporary(fileSource,answerDto);
                } catch (Exception e){
                  e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        //then
        Team result = teamRepository.findById(team.getId()).get();
    }

}