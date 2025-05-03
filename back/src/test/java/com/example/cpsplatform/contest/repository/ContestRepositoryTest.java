package com.example.cpsplatform.contest.repository;

import com.example.cpsplatform.contest.Contest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ContestRepositoryTest {

    @Autowired
    private ContestRepository contestRepository;

    @DisplayName("페이지네이션을 적용하여 대회 목록을 조회하면 해당 페이지의 대회만 반환된다")
    @Test
    void findContestListWithFirstPage() {
        //given
        Contest contest2 = Contest.builder()
                .title("테스트 대회 2")
                .description("테스트 대회 설명 2")
                .season(17)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();

        Contest contest3 = Contest.builder()
                .title("테스트 대회 3")
                .description("테스트 대회 설명 3")
                .season(18)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();

        Contest contest4 = Contest.builder()
                .title("테스트 대회 4")
                .description("테스트 대회 설명 4")
                .season(19)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();

        contestRepository.saveAll(List.of(contest2, contest3, contest4));

        Pageable pageRequest = PageRequest.of(0, 2); //첫 페이지, 페이지당 2개 항목

        //when
        Page<Contest> contestPage = contestRepository.findContestList(pageRequest);
        List<Contest> contests = contestPage.getContent();

        //then
        assertThat(contestPage.getTotalElements()).isEqualTo(3);
        assertThat(contestPage.getTotalPages()).isEqualTo(2);
        assertThat(contests).isNotNull().hasSize(2);
        assertThat(contests).extracting("season")
                .containsExactly(17, 18);
    }

    @DisplayName("페이지네이션을 적용하여 대회 목록의 마지막 페이지를 조회하면 남은 대회들이 반환된다")
    @Test
    void findContestListWithLastPage() {
        //given
        //기존 대회 외에 추가 대회 생성
        Contest contest2 = Contest.builder()
                .title("테스트 대회 2")
                .description("테스트 대회 설명 2")
                .season(17)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();

        Contest contest3 = Contest.builder()
                .title("테스트 대회 3")
                .description("테스트 대회 설명 3")
                .season(18)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();

        Contest contest4 = Contest.builder()
                .title("테스트 대회 4")
                .description("테스트 대회 설명 4")
                .season(19)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();

        contestRepository.saveAll(List.of(contest2, contest3, contest4));

        Pageable pageRequest = PageRequest.of(1, 2);

        //when
        Page<Contest> contestPage = contestRepository.findContestList(pageRequest);
        List<Contest> contests = contestPage.getContent();

        //then
        assertThat(contestPage.getTotalElements()).isEqualTo(3);
        assertThat(contestPage.getTotalPages()).isEqualTo(2);
        assertThat(contests).isNotNull().hasSize(1);
        assertThat(contests).extracting("season")
                .containsExactly( 19);
    }

    @DisplayName("빈 페이지를 요청하면 빈 결과가 반환된다")
    @Test
    void findContestListWithEmptyPage() {
        //given
        Pageable pageRequest = PageRequest.of(2, 2);

        //when
        Page<Contest> contestPage = contestRepository.findContestList(pageRequest);

        //then
        assertThat(contestPage.getTotalElements()).isEqualTo(0);
        assertThat(contestPage.getTotalPages()).isEqualTo(0);
        assertThat(contestPage.getContent()).isEmpty();
        assertThat(contestPage.hasContent()).isFalse();
    }


    @DisplayName("가장 최근의 대회의 정보를 가져온다.")
    @Test
    void findLatestContest() {
        //given
        Contest contest1 = Contest.builder()
                .title("테스트 대회 1")
                .description("테스트 대회 설명 1")
                .season(16)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();

        Contest contest2 = Contest.builder()
                .title("테스트 대회 2")
                .description("테스트 대회 설명 2")
                .season(17)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();

        Contest contest3 = Contest.builder()
                .title("테스트 대회 3")
                .description("테스트 대회 설명 3")
                .season(18)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();

        Contest contest4 = Contest.builder()
                .title("테스트 대회 4")
                .description("테스트 대회 설명 4")
                .season(19)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();
        contestRepository.saveAll(List.of(contest1,contest2, contest3, contest4));

        //when
        Contest contest = contestRepository.findLatestContest().get();

        //then
        assertThat(contest).isEqualTo(contest4);
    }


}