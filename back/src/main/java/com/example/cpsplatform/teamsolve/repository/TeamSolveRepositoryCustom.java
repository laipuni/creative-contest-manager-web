package com.example.cpsplatform.teamsolve.repository;

import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListDto;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListResponse;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerDto;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;

import java.util.List;

public interface TeamSolveRepositoryCustom {

    public List<GetTeamAnswerDto> findSubmittedAnswersByTeamId(Long teamId, TeamSolveType teamSolveType);

    public TeamSolveListResponse findTeamSolveByAdminCond(final Long teamId, final TeamSolveType teamSolveType);
}
