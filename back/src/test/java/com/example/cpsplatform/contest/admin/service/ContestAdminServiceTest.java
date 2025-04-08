package com.example.cpsplatform.contest.admin.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.DuplicateDataException;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContestAdminServiceTest {

    @Autowired
    ContestAdminService contestAdminService;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    EntityManager entityManager;


    @BeforeEach
    void tearUp(){
        contestRepository.deleteAllInBatch();
    }

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

}