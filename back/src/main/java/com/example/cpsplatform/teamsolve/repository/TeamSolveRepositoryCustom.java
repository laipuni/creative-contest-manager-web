package com.example.cpsplatform.teamsolve.repository;

import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerDto;

import java.util.List;

public interface TeamSolveRepositoryCustom {

    public List<GetTeamAnswerDto> findSubmittedAnswersByTeamId(Long teamId);

}
