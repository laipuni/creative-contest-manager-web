package com.example.cpsplatform.teamsolve.admin.controller.response;

import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamSolveListResponse {

    private List<TeamSolveListDto> teamSolveListDtos;

}
