package com.example.cpsplatform.contest.controller.service;


import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.ContestJoinException;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        if (username == null || username.isBlank()) {
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

}
