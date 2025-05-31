package com.example.cpsplatform.contest;

import com.example.cpsplatform.finalcontest.FinalContest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContestTest {

    @Transactional
    @DisplayName("대회를 생성한다.")
    @Test
    void createContest(){
        //given
        String title = "title";
        int season = 1;
        String description ="대회 설명";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);

        //when
        //then
        FinalContest finalContest = FinalContest.builder()
                .title("본선 대회")
                .location("장소")
                .startTime(now.plusDays(4))
                .endTime(now.plusDays(5))
                .build();

        Contest contest = Contest.of(title,description,season,registrationStartAt,
                registrationEndAt,contestStartAt,contestEndAt,finalContest);

        //then
        assertThat(contest)
                .extracting("title", "season", "description", "registrationStartAt", "registrationEndAt",
                        "startTime","endTime")
                .containsExactly(title,season,description,registrationStartAt,
                        registrationEndAt,contestStartAt,contestEndAt);
    }

    @Transactional
    @DisplayName("대회를 생성할 때, 마감 시간이 시작 시간보다 앞일 경우 예외가 발생한다.")
    @Test
    void createContestWithEndAtisBeforeStartAt(){
        //given
        String title = "title";
        int season = 1;
        String description ="대회 설명";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(4);
        LocalDateTime contestEndAt = now.plusDays(3); //대회 마감시간이 시작보다 앞이도록 설정

        FinalContest finalContest = FinalContest.builder()
                .title("본선 대회")
                .location("장소")
                .startTime(now.plusDays(4))
                .endTime(now.plusDays(5))
                .build();

        //when
        //then
        assertThatThrownBy(() -> Contest.of(title,description,season,registrationStartAt,
                registrationEndAt,contestStartAt,contestEndAt,finalContest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("대회 종료 시간은 대회 시작 시간 이후여야 합니다.");
    }

    @Transactional
    @DisplayName("대회를 생성할 때, 점수 마감 시간이 접수 시작 시간보다 앞일 경우 예외가 발생한다.")
    @Test
    void createContestWithRegistrationEndAtisBeforeStartAt(){
        //given
        String title = "title";
        int season = 1;
        String description ="대회 설명";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(2);
        LocalDateTime registrationEndAt= now.plusDays(1);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4); //대회 마감시간이 시작보다 앞이도록 설정

        FinalContest finalContest = FinalContest.builder()
                .title("본선 대회")
                .location("장소")
                .startTime(now.plusDays(4))
                .endTime(now.plusDays(5))
                .build();

        //when
        //then
        assertThatThrownBy(() -> Contest.of(title,description,season,registrationStartAt,
                registrationEndAt,contestStartAt,contestEndAt,finalContest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("접수 종료 시간은 접수 시작 시간 이후여야 합니다.");
    }

    @Transactional
    @DisplayName("대회를 생성할 때, 점수 마감 시간이 대회 시작 시간보다 앞일 경우 예외가 발생한다.")
    @Test
    void createContestWithRegistrationEndAfterStartTime(){
        //given
        String title = "title";
        int season = 1;
        String description ="대회 설명";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.minusDays(2);
        LocalDateTime registrationEndAt= now.minusDays(1);
        LocalDateTime contestStartAt = now.minusDays(2);
        LocalDateTime contestEndAt = now.plusDays(4); //대회 마감시간이 시작보다 앞이도록 설정

        FinalContest finalContest = FinalContest.builder()
                .title("본선 대회")
                .location("장소")
                .startTime(now.plusDays(4))
                .endTime(now.plusDays(5))
                .build();

        //when
        //then
        assertThatThrownBy(() -> Contest.of(title,description,season,registrationStartAt,
                registrationEndAt,contestStartAt,contestEndAt,finalContest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("대회 시작 시간은 접수 마감 시간 이후여야 합니다.");
    }

    @Transactional
    @DisplayName("대회의 정보를 수정한다.")
    @Test
    void updateContest(){
        //given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        //contest 생성
        Contest contest = Contest.builder()
                .title("title")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();

        String updatedTitle = "title";
        int updatedSeason = 1;
        String updatedDescription ="대회 설명";
        LocalDateTime updatedRegistrationStartAt = now.plusDays(1);
        LocalDateTime updatedRegistrationEndAt= now.plusDays(2);
        LocalDateTime updatedContestStartAt = now.plusDays(3);
        LocalDateTime updatedContestEndAt = now.plusDays(4);


        //when
        contest.updateContest(
                updatedTitle,updatedDescription,updatedSeason,updatedRegistrationStartAt,
                updatedRegistrationEndAt,updatedContestStartAt,updatedContestEndAt
        );

        //then
        assertThat(contest)
                .extracting("title", "season", "description", "registrationStartAt", "registrationEndAt",
                        "startTime","endTime")
                .containsExactly(updatedTitle,updatedSeason,updatedDescription,updatedRegistrationStartAt,
                        updatedRegistrationEndAt,updatedContestStartAt,updatedContestEndAt);
    }

    @Transactional
    @DisplayName("대회의 시작 시간과 마감 시간을 수정할 때, 마감 시간이 시작 시간보다 앞일 경우 예외가 발생한다.")
    @Test
    void updateContestWithEndAtisBeforeStartAt(){
        //given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        //contest 생성
        Contest contest = Contest.builder()
                .title("title")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();

        String updatedTitle = "title";
        int updatedSeason = 1;
        String updatedDescription ="대회 설명";
        LocalDateTime updatedRegistrationStartAt = now.plusDays(1);
        LocalDateTime updatedRegistrationEndAt= now.plusDays(2);
        LocalDateTime updatedContestStartAt = now.plusDays(4);
        LocalDateTime updatedContestEndAt = now.plusDays(3); // 대회 마감이 시작보다 앞으로 설정


        //when
        //then
        assertThatThrownBy(() -> contest.updateContest(
                updatedTitle,updatedDescription,updatedSeason,updatedRegistrationStartAt,
                updatedRegistrationEndAt,updatedContestStartAt,updatedContestEndAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("대회 종료 시간은 대회 시작 시간 이후여야 합니다.");

    }

    @Transactional
    @DisplayName("대회 접수 시작 시간과 마감 시간을 수정할 때, 점수 마감 시간이 접수 시작 시간보다 앞일 경우 예외가 발생한다.")
    @Test
    void updateContestWithRegistrationEndAtisBeforeStartAt(){
        //given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        //contest 생성
        Contest contest = Contest.builder()
                .title("title")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();

        String updatedTitle = "title";
        int updatedSeason = 1;
        String updatedDescription ="대회 설명";
        LocalDateTime updatedRegistrationStartAt = now.plusDays(2);
        LocalDateTime updatedRegistrationEndAt= now.plusDays(1);// 대회 마감이 시작보다 앞으로 설정
        LocalDateTime updatedContestStartAt = now.plusDays(3);
        LocalDateTime updatedContestEndAt = now.plusDays(4);


        //when
        //then
        assertThatThrownBy(() -> contest.updateContest(
                updatedTitle,updatedDescription,updatedSeason,updatedRegistrationStartAt,
                updatedRegistrationEndAt,updatedContestStartAt,updatedContestEndAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("접수 종료 시간은 접수 시작 시간 이후여야 합니다.");
    }


    @DisplayName("현재 시간이 대회 시작 시간 이전인 경우 true를 반환한다")
    @Test
    void isNotOngoingWithBeforeStartTime() {
        //given
        LocalDateTime now = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 2, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 3, 10, 0);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(now.minusDays(5))
                .registrationEndAt(now.minusDays(2))
                .startTime(startTime)
                .endTime(endTime)
                .build();

        //when
        boolean result = contest.isNotOngoing(now);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("현재 시간이 대회 종료 시간 이후인 경우 true를 반환한다")
    @Test
    void isNotOngoingWithAfterEndTime() {
        //given
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 3, 10, 0);
        LocalDateTime now = LocalDateTime.of(2023, 1, 4, 10, 0);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(startTime.minusDays(5))
                .registrationEndAt(startTime.minusDays(2))
                .startTime(startTime)
                .endTime(endTime)
                .build();

        //when
        boolean result = contest.isNotOngoing(now);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("현재 시간이 대회 시작 시간과 종료 시간 사이인 경우 false를 반환한다")
    @Test
    void isNotOngoingWithBetweenStartAndEndTime() {
        //given
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime now = LocalDateTime.of(2023, 1, 2, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 3, 10, 0);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(startTime.minusDays(5))
                .registrationEndAt(startTime.minusDays(2))
                .startTime(startTime)
                .endTime(endTime)
                .build();

        //when
        boolean result = contest.isNotOngoing(now);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("현재 시간이 대회 시작 시간과 정확히 같은 경우 false를 반환한다")
    @Test
    void isNotOngoingWithExactlyStartTime() {
        //given
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime now = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 3, 10, 0);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(startTime.minusDays(5))
                .registrationEndAt(startTime.minusDays(2))
                .startTime(startTime)
                .endTime(endTime)
                .build();

        //when
        boolean result = contest.isNotOngoing(now);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("현재 시간이 대회 종료 시간과 정확히 같은 경우 false를 반환한다")
    @Test
    void isNotOngoingWithExactlyEndTime() {
        //given
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 3, 10, 0);
        LocalDateTime now = LocalDateTime.of(2023, 1, 3, 10, 0);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(startTime.minusDays(5))
                .registrationEndAt(startTime.minusDays(2))
                .startTime(startTime)
                .endTime(endTime)
                .build();

        //when
        boolean result = contest.isNotOngoing(now);

        //then
        assertThat(result).isFalse();
    }
}