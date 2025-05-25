package com.example.cpsplatform.teamsolve.admin;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveDetailResponse;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListResponse;
import com.example.cpsplatform.teamsolve.admin.service.TeamSolveAdminService;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamSolveAdminController {

    private final TeamSolveAdminService teamSolveAdminService;

    @AdminLog
    @GetMapping("/api/admin/v1/teams/{teamId}/team-solves")
    public ApiResponse<TeamSolveListResponse> getTeamSolveByTeam(@PathVariable("teamId")Long teamId,
                                                                 @RequestParam(value = "team_solve_type",defaultValue = "") String teamSolveType){
        TeamSolveType type = TeamSolveType.findTeamSolveType(teamSolveType);
        TeamSolveListResponse response = teamSolveAdminService.getTeamSolveByTeam(teamId, type);
        return ApiResponse.ok(response);
    }

    @AdminLog
    @GetMapping("/api/admin/v1/teams/{teamId}/team-solves/{teamSolveId}")
    public ApiResponse<TeamSolveDetailResponse> getTeamSolveDetail(@PathVariable("teamId")Long teamId,
                                                                   @PathVariable("teamSolveId")Long teamSolveId){
        TeamSolveDetailResponse response = teamSolveAdminService.getTeamSolveDetail(teamId, teamSolveId);
        return ApiResponse.ok(response);
    }
}
