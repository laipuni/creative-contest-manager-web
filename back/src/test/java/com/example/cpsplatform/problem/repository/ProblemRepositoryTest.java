package com.example.cpsplatform.problem.repository;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
@SpringBootTest
class ProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ContestRepository contestRepository;

    private Contest contest; // 테스트에서 사용할 contest

    @AfterEach
    void tearDown(){
        problemRepository.deleteAllInBatch();
        contestRepository.deleteAllInBatch();
    }

    @BeforeEach
    void tearUp() {
        //테스트용 Contest 생성
        contest = Contest.builder()
                .title("테스트 대회")
                .description("테스트 대회 설명")
                .season(16)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();
        contestRepository.save(contest);

        //다양한 섹션과 순서를 가진 문제들 생성
        createProblem("문제 1", contest, Section.COMMON, 1, ProblemType.CONTEST);
        createProblem("문제 2", contest, Section.COMMON, 2, ProblemType.CONTEST);
        createProblem("문제 3", contest, Section.ELEMENTARY_MIDDLE, 1, ProblemType.CONTEST);
        createProblem("문제 4", contest, Section.HIGH_NORMAL, 1, ProblemType.CONTEST);

        //필터링 테스트를 위한 다른 대회의 문제 생성
        Contest anotherContest = Contest.builder()
                .title("다른 테스트 대회")
                .description("다른 테스트 대회 설명")
                .season(17)
                .registrationStartAt(now().minusDays(7))
                .registrationEndAt(now().minusDays(2))
                .startTime(now().plusDays(1))
                .endTime(now().plusDays(2))
                .build();
        contestRepository.save(anotherContest);

        createProblem("다른 대회 문제", anotherContest, Section.HIGH_NORMAL, 1, ProblemType.CONTEST);

        problemRepository.flush();
    }

    private void createProblem(String title, Contest contest, Section section, Integer order, ProblemType type) {
        Problem problem = Problem.builder()
                .title(title)
                .contest(contest)
                .section(section)
                .problemOrder(order)
                .problemType(type)
                .content("문제 설명")
                .build();
        problemRepository.save(problem);
    }

    @DisplayName("대회 ID로 문제를 조회하면 섹션과 문제 순서대로 정렬되어 반환된다")
    @Test
    void findContestProblemsByContestAndProblemTypeWithNotValidContestId() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Long contestId = contest.getId();

        //when
        Page<Problem> problemPage = problemRepository.findContestProblemsByContestAndProblemType(pageRequest, ProblemType.CONTEST, contestId);
        List<Problem> problems = problemPage.getContent();

        //then
        assertThat(problems).isNotNull().hasSize(4)
                .extracting("title","section", "problemOrder","contest.id")
                .containsExactly(
                        tuple("문제 1",Section.COMMON, 1,contestId),
                        tuple("문제 2",Section.COMMON, 2,contestId),
                        tuple("문제 3",Section.ELEMENTARY_MIDDLE, 1,contestId),
                        tuple("문제 4",Section.HIGH_NORMAL, 1,contestId)
                );
    }

    @DisplayName("페이지네이션을 적용하여 대회 문제를 조회하면 해당 페이지의 문제만 반환된다")
    @Test
    void findContestProblemsByContestAndProblemTypeFirstPage() {
        //given
        Pageable pageRequest = PageRequest.of(0, 2); // 첫 페이지, 페이지당 2개 항목
        Long contestId = contest.getId();

        //when
        Page<Problem> problemPage = problemRepository.findContestProblemsByContestAndProblemType(
                pageRequest, ProblemType.CONTEST, contestId);
        List<Problem> problems = problemPage.getContent();

        //then
        assertThat(problemPage.getTotalElements()).isEqualTo(4);
        assertThat(problemPage.getTotalPages()).isEqualTo(2);
        assertThat(problems).isNotNull().hasSize(2).
                extracting("title")
                .containsExactly("문제 1", "문제 2");

    }

    @DisplayName("페이지네이션을 적용하여 대회 문제를 조회하면 해당 페이지의 문제만 반환된다")
    @Test
    void findContestProblemsByContestAndProblemTypeLastPage() {
        //given
        Pageable secondPageable = PageRequest.of(1, 2);
        Long contestId = contest.getId();

        //when
        Page<Problem> problems = problemRepository.findContestProblemsByContestAndProblemType(
                secondPageable,ProblemType.CONTEST, contestId);
        List<Problem> result = problems.getContent();

        //then
        assertThat(problems).isNotNull().hasSize(2);
        assertThat(problems.getTotalElements()).isEqualTo(4);
        assertThat(problems.getTotalPages()).isEqualTo(2);
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result).extracting("title")
                .containsExactly("문제 3", "문제 4");
    }

    @DisplayName("존재하지 않는 대회 ID로 조회하면 빈 페이지가 반환된다")
    @Test
    void findContestProblemsByContestAndProblemTypeWithNonExistentContestId() {
        //given
        Pageable pageRequest = PageRequest.of(0, 10);
        Long nonExistentContestId = 99999L;

        //when
        Page<Problem> problemPage = problemRepository.findContestProblemsByContestAndProblemType(
                pageRequest, ProblemType.CONTEST, nonExistentContestId);

        //then
        assertThat(problemPage).isNotNull();
        assertThat(problemPage.getContent()).isEmpty();
        assertThat(problemPage.getTotalElements()).isEqualTo(0);
    }

}