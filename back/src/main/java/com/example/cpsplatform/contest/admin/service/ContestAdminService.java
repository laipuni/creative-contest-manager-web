package com.example.cpsplatform.contest.admin.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.admin.controller.response.ContestDetailResponse;
import com.example.cpsplatform.contest.admin.controller.response.ContestListResponse;
import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestDeleteDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestUpdateDto;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.problem.domain.Problem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestAdminService {

    public static final int CONTEST_PAGE_SIZE = 5;

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

    @Transactional
    public void deleteContest(ContestDeleteDto deleteDto) {
        contestRepository.deleteById(deleteDto.getContestId());
        log.info("[ADMIN] 대회 삭제: id={}", deleteDto.getContestId());
    }

    public ContestListResponse searchContestList(final int page) {
        Pageable pageable = PageRequest.of(page,CONTEST_PAGE_SIZE);
        Page<Contest> result = contestRepository.findContestList(pageable);
        return ContestListResponse.of(result);
    }

    public ContestDetailResponse findContestDetail(final Long contestId) {
        log.debug("대회({})를 조회 시도",contestId);
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회는 존재하지 않습니다."));
        return ContestDetailResponse.of(contest);
    }
}
