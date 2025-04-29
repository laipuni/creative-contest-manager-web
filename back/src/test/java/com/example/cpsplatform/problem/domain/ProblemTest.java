package com.example.cpsplatform.problem.domain;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProblemTest {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void tearUp(){
        problemRepository.deleteAllInBatch();
        contestRepository.deleteAllInBatch();
    }

    @DisplayName("만약 동일한 대회,섹션,문제 번호가 존재할 경우 예외가 발생한다.")
    @Test
    void uniqueConstraintsTest(){
        //given
        String title = "title";
        int season = 1;
        String description ="대회 설명";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        Contest contest = Contest.of(
                title,description,season,registrationStartAt
                ,registrationEndAt, contestStartAt,contestEndAt
        );

        contestRepository.save(contest);

        Problem problem = Problem.builder()
                .title("문제 1")
                .content("문제 1 설명")
                .contest(contest)
                .problemType(ProblemType.CONTEST)
                .problemOrder(1)
                .section(Section.COMMON)
                .build();
        problemRepository.save(problem);

        Problem sameProblem = Problem.builder()
                .title("같은 문제 1")
                .content("같은 문제 1 설명")
                .contest(contest)
                .problemType(ProblemType.CONTEST)
                .problemOrder(1)
                .section(Section.COMMON)
                .build();

        //when
        //then
        assertThatThrownBy(() -> problemRepository.save(sameProblem))//같은 문제 재등록
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("예선 출제용 문제를 생성할 때, 대회가 null일 경우 예외가 발생한다.")
    @Test
    void createContestProblemWithNotContest(){
        //given
        //when
        //then
        assertThatThrownBy(() ->
                Problem.createContestProblem(
                        "문제 제목",
                        null,
                        Section.COMMON,
                        "문제 설명",
                        1
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("출제용 문제는 출제할 대회 정보가 필수입니다.");
    }

    @DisplayName("예선 출제용 문제를 생성할 때, 문제 번호가 null일 경우 예외가 발생한다.")
    @Test
    void createContestProblemWithNotProblemOrder(){
        //given
        String title = "title";
        int season = 1;
        String description ="대회 설명";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        Contest contest = Contest.of(
                title,description,season,registrationStartAt
                ,registrationEndAt, contestStartAt,contestEndAt
        );
        //when
        //then
        assertThatThrownBy(() ->
                Problem.createContestProblem(
                        "문제 제목",
                        contest,
                        Section.COMMON,
                        "문제 설명",
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("출제용 문제는 문제번호가 필수입니다.");
    }

}