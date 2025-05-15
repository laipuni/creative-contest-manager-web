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
import java.util.Optional;

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

    @DisplayName("임시 삭제된 대회를 ID로 단건 조회할 수 있다")
    @Test
    void findDeletedContestById() {
        //given
        Contest deleted = Contest.builder()
                .title("삭제된 대회")
                .description("삭제 테스트")
                .season(99)
                .registrationStartAt(now().minusDays(3))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .deleted(true)
                .build();

        Contest saved = contestRepository.save(deleted);

        // when
        Optional<Contest> found = contestRepository.findDeletedContestById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("삭제된 대회");
        assertThat(found.get().isDeleted()).isTrue();
    }

    @DisplayName("임시 삭제된 대회 목록을 전체 조회할 수 있다")
    @Test
    void findAllDeletedContests() {
        //given
        Contest deleted1 = Contest.builder()
                .title("삭제 대회 1")
                .description("설명1")
                .season(101)
                .registrationStartAt(now().minusDays(4))
                .registrationEndAt(now().minusDays(2))
                .startTime(now())
                .endTime(now().plusDays(2))
                .deleted(true)
                .build();

        Contest deleted2 = Contest.builder()
                .title("삭제 대회 2")
                .description("설명2")
                .season(102)
                .registrationStartAt(now().minusDays(4))
                .registrationEndAt(now().minusDays(2))
                .startTime(now())
                .endTime(now().plusDays(2))
                .deleted(true)
                .build();

        contestRepository.saveAll(List.of(deleted1, deleted2));

        // when
        List<Contest> deletedContests = contestRepository.findDeletedContestById();

        // then
        assertThat(deletedContests).hasSize(2);
        assertThat(deletedContests).extracting("title")
                .containsExactlyInAnyOrder("삭제 대회 1", "삭제 대회 2");
    }

    @DisplayName("임시 삭제된 대회를 실제로 DB에서 완전히 삭제할 수 있다")
    @Test
    void hardDeleteDeletedContest() {
        //given
        Contest deleted = Contest.builder()
                .title("삭제 대상 대회")
                .description("삭제 대상 설명")
                .season(103)
                .registrationStartAt(now().minusDays(4))
                .registrationEndAt(now().minusDays(2))
                .startTime(now())
                .endTime(now().plusDays(2))
                .deleted(true)
                .build();

        Contest saved = contestRepository.save(deleted);

        Long contestId = saved.getId();

        //when
        contestRepository.hardDeleteById(contestId);

        //then
        Optional<Contest> afterDelete = contestRepository.findDeletedContestById(contestId);
        assertThat(afterDelete).isEmpty();
    }

}