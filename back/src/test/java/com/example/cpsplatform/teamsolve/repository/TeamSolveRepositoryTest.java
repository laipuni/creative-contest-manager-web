package com.example.cpsplatform.teamsolve.repository;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TeamSolveRepositoryTest {

    @Autowired
    TeamSolveRepository teamSolveRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TeamSolveTestExecutor teamSolveTestExecutor;

    //테스트를 위한 초기 설정
    TeamSolve teamSolve;
    Team team;
    Problem problem;

    @BeforeEach
    void tearUp(){
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("teamsolveRepo@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01056791234")
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

        team = Team.builder().name("팀 이름").winner(false).teamNumber("001").leader(member).contest(contest).build();
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

        teamSolve = TeamSolve.builder()
                .team(team)
                .problem(problem)
                .build();
        teamSolveRepository.save(teamSolve);
    }

    @AfterEach
    void tearDown(){
        teamSolveRepository.deleteAllInBatch();
        problemRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        contestRepository.deleteAllInBatch();
    }
// 운영방식의 변화로 수정횟수를 TeamSolve에서 Team으로 이동함
//    @DisplayName("100명의 쓰레드가 문제 id와 팀 id로 pessimistic lock을 걸어 조회하고 수정횟수를 100까지 증가시킨다.")
//    @Test
//    void findByTeamIdAndProblemId() throws InterruptedException {
//        //given
//        int threadCount = 100;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        //when
//        for (int i = 0; i < threadCount; i++) {
//            executorService.submit(() -> {
//                try {
//                    teamSolveTestExecutor.incrementModifyCount(team.getId(), problem.getId());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//        latch.await(); // 모든 쓰레드 완료 대기
//
//        //then
//        List<TeamSolve> result = teamSolveRepository.findAll();
//        assertThat(result.get(0).getModifyCount()).isEqualTo(threadCount);
//    }


}