package com.example.cpsplatform.teamsolve.admin.service;

import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListResponse;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamSolveAdminService {

    private final TeamSolveRepository teamSolveRepository;

    public TeamSolveListResponse getTeamSolveByTeam(final Long teamId, final TeamSolveType teamSolveType) {
        return teamSolveRepository.findTeamSolveByAdminCond(teamId,teamSolveType);
    }
}
