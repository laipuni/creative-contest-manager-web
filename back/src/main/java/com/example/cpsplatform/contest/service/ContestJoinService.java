package com.example.cpsplatform.contest.service;


import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.controller.response.LatestContestResponse;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.ContestJoinException;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestJoinService {

    private final ContestRepository contestRepository;
    private final MemberTeamRepository memberTeamRepository;

    /**
     * 현재 대회에 참여할 자격과 요건을 갖추었는지 확인하는 메서드
     * 해당 대회에 소속된 팀이 없거나 혹은 대회 진행 중이 아닌 경우 예외가 발생한다.
     */
    public void validateContestParticipation(final Long contestId, final String username, final LocalDateTime now){
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("사용자 정보가 없습니다.");
        }

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회는 존재하지 않습니다."));

        boolean result = memberTeamRepository.existsByContestIdAndLoginId(contestId, username);
        if(!result){
            //만약 해당 대회 소속된 팀이 없을 경우
            throw new ContestJoinException("제"+ contest.getSeason()+"회 대회에 소속된 팀이 없습니다.");
        }

        if(contest.isNotOngoing(now)){
            //대회 진행 중이 아닌 경우
            throw new ContestJoinException("제"+ contest.getSeason()+"회 대회는 현재 개최 기간이 아닙니다.");
        }
    }

    public LatestContestResponse getLatestContestInfo() {
        log.trace("유저의 권한에서 최근 대회 정보 조회 시도");
        Contest contest = contestRepository.findLatestContest()
                .orElseThrow(() -> new IllegalArgumentException("최신 대회의 정보를 받아오는데 실패했습니다."));
        return LatestContestResponse.of(contest);
    }
}
