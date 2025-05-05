package com.example.cpsplatform.contest.admin.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.admin.request.DeleteContestRequest;
import com.example.cpsplatform.contest.admin.request.UpdateContestRequest;
import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestDeleteDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestUpdateDto;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.DuplicateDataException;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ContestAdminServiceTest {

    @Autowired
    ContestAdminService contestAdminService;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    EntityManager entityManager;

    @Transactional
    @DisplayName("대회의 정보를 받아서 대회를 생성 및 저장한다.")
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
        ContestCreateDto contestCreateDto = new ContestCreateDto(title,season,description,
                registrationStartAt,registrationEndAt,contestStartAt,contestEndAt);
        //when
        contestAdminService.createContest(contestCreateDto);
        List<Contest> result = contestRepository.findAll();
        //then
        assertThat(result.get(0))
                .extracting("title", "season", "description", "registrationStartAt", "registrationEndAt",
                        "startTime","endTime")
                .containsExactly(title,season,description,registrationStartAt,
                        registrationEndAt,contestStartAt,contestEndAt);
    }

    @DisplayName("수정할 대회가 존재하지 않으면 예외가 발생한다.")
    @Test
    void updateContestWithNotExistContest(){
        //given
        Long invalidContestId = 2025L;
        String title = "title";
        int season = 1;
        String description ="대회 설명";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        ContestUpdateDto updateDto = new ContestUpdateDto(invalidContestId,title,season,description,
                registrationStartAt,registrationEndAt,contestStartAt,contestEndAt);
        //when
        //then
        assertThatThrownBy(() -> contestAdminService.updateContest(updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("수정할 대회가 존재하지 않습니다.");
    }

    @Transactional
    @DisplayName("수정할 대회가 존재하지 않으면 예외가 발생한다.")
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

        contestRepository.save(contest);

        //수정할 내용 선언
        String updatedTitle = "updatedTitle";
        int updatedSeason = 2;
        String updatedDescription ="수정된 대회 설명";
        LocalDateTime updatedRegistrationStartAt = now.plusDays(2);
        LocalDateTime updatedRegistrationEndAt= now.plusDays(3);
        LocalDateTime updatedContestStartAt = now.plusDays(4);
        LocalDateTime updatedContestEndAt = now.plusDays(5);

        ContestUpdateDto updateDto = new ContestUpdateDto(contest.getId(),updatedTitle,updatedSeason,updatedDescription,
                updatedRegistrationStartAt,updatedRegistrationEndAt,updatedContestStartAt,updatedContestEndAt);
        //when
        contestAdminService.updateContest(updateDto);
        List<Contest> result = contestRepository.findAll();
        //then
        assertThat(result.get(0))
                .extracting("title", "season", "description", "registrationStartAt", "registrationEndAt",
                        "startTime","endTime")
                .containsExactly(updatedTitle,updatedSeason,updatedDescription,updatedRegistrationStartAt,
                        updatedRegistrationEndAt,updatedContestStartAt,updatedContestEndAt);
    }

    @Transactional
    @DisplayName("수정할 대회가 존재하지 않으면 예외가 발생한다.")
    @Test
    void deleteContest(){
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

        contestRepository.save(contest);

        ContestDeleteDto request = new ContestDeleteDto(contest.getId());

        //when
        contestAdminService.deleteContest(request);
        List<Contest> result = contestRepository.findAll();
        //then
        assertThat(result).hasSize(0);
    }

    @DisplayName("임시 삭제된 대회를 복구한다.")
    @Test
    void recoverContest(){
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
                .season(16)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();
        //대회를 미리 저장
        Contest save = contestRepository.save(contest);
        entityManager.flush();
        entityManager.clear();

        //미리 저장한 대회를 소프트 삭제
        contestRepository.deleteById(contest.getId());
        entityManager.flush();
        entityManager.clear();

        //when
        contestAdminService.recoverContest(contest.getId());

        //then
        List<Contest> result = contestRepository.findAll();
        assertThat(result).hasSize(1)
                .extracting("id",
                        "title",
                        "description",
                        "season"
                )
                .containsExactly(
                        tuple(save.getId(),
                                save.getTitle(),
                                save.getDescription(),
                                save.getSeason()
                        )
                );
    }

}