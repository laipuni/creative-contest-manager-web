package com.example.cpsplatform.contest;

import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import jakarta.persistence.Column;
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
        Contest contest = Contest.of(title,description,season,registrationStartAt,
                registrationEndAt,contestStartAt,contestEndAt);

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

        //when
        //then
        assertThatThrownBy(() -> Contest.of(title,description,season,registrationStartAt,
                registrationEndAt,contestStartAt,contestEndAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("대회 종료 시간은 대회 시작 시간보다 이후여야 합니다.");
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

        //when
        //then
        assertThatThrownBy(() -> Contest.of(title,description,season,registrationStartAt,
                registrationEndAt,contestStartAt,contestEndAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("접수 종료 기간은 접수 시작 기간보다 이후여야 합니다.");
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
                .hasMessageMatching("대회 종료 시간은 대회 시작 시간보다 이후여야 합니다.");

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
                .hasMessageMatching("접수 종료 기간은 접수 시작 기간보다 이후여야 합니다.");
    }
}