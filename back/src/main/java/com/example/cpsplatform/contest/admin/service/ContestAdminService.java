package com.example.cpsplatform.contest.admin.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestUpdateDto;
import com.example.cpsplatform.contest.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestAdminService {

    private final ContestRepository contestRepository;

    @Transactional
    public void createContest(ContestCreateDto createDto){
        Contest contest = contestRepository.save(createDto.toEntity());
        log.info("[ADMIN] 대회 생성: id={}", contest.getId());
    }

    @Transactional
    public void updateContest(final ContestUpdateDto updateDto) {
        Contest contest = contestRepository.findById(updateDto.getContestId())
                .orElseThrow(() -> new IllegalArgumentException("수정할 대회가 존재하지 않습니다."));

        contest.updateContest(
                    updateDto.getTitle(), updateDto.getDescription(),updateDto.getSeason(),
                    updateDto.getRegistrationStartAt(),updateDto.getRegistrationEndAt(),
                    updateDto.getContestStartAt(),updateDto.getContestEndAt()
        );

        log.info("[ADMIN] 대회 변경: id={}", contest.getId());
    }
}
