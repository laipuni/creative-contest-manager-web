package com.example.cpsplatform.contest.admin.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.admin.controller.response.*;
import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestDeleteDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestUpdateDto;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamnumber.domain.TeamNumber;
import com.example.cpsplatform.teamnumber.repository.TeamNumberRepository;
import java.util.List;
import java.util.Optional;

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

    public static final int CONTEST_PAGE_SIZE = 10;
    public static final String  ADMIN_CONTEST_LOG = "[CONTESTADMIN]";

    private final ContestRepository contestRepository;
    private final TeamRepository teamRepository;
    private final TeamNumberRepository teamNumberRepository;

    @Transactional
    public void createContest(ContestCreateDto createDto){
        Contest contest = contestRepository.save(createDto.toEntity());
        log.info("{} 대회(id: {})를 생성했습니다.", ADMIN_CONTEST_LOG, contest.getId());
        TeamNumber teamNumber = teamNumberRepository.save(TeamNumber.of(contest, 0));
        log.info("{} 대회(id: {})의 팀 접수 번호(id: {})를 생성했습니다.", ADMIN_CONTEST_LOG, contest.getId(), teamNumber.getId());
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

        log.info("{} '{}' 대회(id: {})를 수정했습니다.", ADMIN_CONTEST_LOG, contest.getTitle(), contest.getId());
    }

    @Transactional
    public void deleteContest(ContestDeleteDto deleteDto) {
        Contest contest = contestRepository.findById(deleteDto.getContestId())
                .orElseThrow(() -> new IllegalArgumentException("삭제할 대회가 존재하지 않습니다."));
        contestRepository.deleteById(deleteDto.getContestId());
        log.info("{} '{}' 대회(id: {})를 삭제했습니다.", ADMIN_CONTEST_LOG, contest.getTitle(), contest.getId());

    }

    public ContestListResponse searchContestList(final int page) {
        Pageable pageable = PageRequest.of(page,CONTEST_PAGE_SIZE);
        Page<Contest> result = contestRepository.findContestList(pageable);
        return ContestListResponse.of(result);
    }

    public ContestDetailResponse findContestDetail(final Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회는 존재하지 않습니다."));
        log.info("{} '{}' 대회(id: {})를 조회했습니다.", ADMIN_CONTEST_LOG, contest.getTitle(), contestId);
        return ContestDetailResponse.of(contest);
    }

    public TeamListByContestResponse searchTeamListByContest(final Long contestId, final int page){
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회는 존재하지 않습니다."));
        Pageable pageable = PageRequest.of(page,CONTEST_PAGE_SIZE);
        Page<Team> teamList = teamRepository.findTeamListByContest(contest, pageable);
        return TeamListByContestResponse.of(teamList);
    }

    public ContestLatestResponse findContestLatest() {
        log.info("{} 최신 대회를 조회 시도",ADMIN_CONTEST_LOG);
        return contestRepository.findLatestContest()
                .map(ContestLatestResponse::of)
                .orElse(null); //프론트가 null 처리하기로 했기 때문에 예외 대신 null 반환
    }

    @Transactional
    public void recoverContest(final Long contestId) {
        Contest contest = contestRepository.findDeletedContestById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("복구할 대회가 존재하지 않습니다."));
        contest.recover();
        log.info("{} 임시 삭제된 {} 대회(id: {})를 복구했습니다.", ADMIN_CONTEST_LOG, contest.getTitle(), contestId);
    }

    public DeletedContestListResponse findDeletedContest() {
        List<Contest> result = contestRepository.findDeletedContestById();
        return DeletedContestListResponse.of(result);
    }
}
