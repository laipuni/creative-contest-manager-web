package com.example.cpsplatform.teamsolve.repository;

import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TeamSolveTestExecutor {

    private final TeamSolveRepository teamSolveRepository;

    public TeamSolveTestExecutor(final TeamSolveRepository teamSolveRepository) {
        this.teamSolveRepository = teamSolveRepository;
    }

    @Transactional
    public void incrementModifyCount(Long teamId, Long problemId) {
        TeamSolve teamSolve = teamSolveRepository.findByTeamIdAndProblemId(teamId, problemId)
                .orElseThrow();
        teamSolve.incrementModifyCount();
    }

}
