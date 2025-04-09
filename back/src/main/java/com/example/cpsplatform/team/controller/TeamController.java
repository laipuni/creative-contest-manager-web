package com.example.cpsplatform.team.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.team.controller.request.CreateTeamRequest;
import com.example.cpsplatform.team.service.TeamService;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/api/team")
    public ApiResponse<Long> createTeam(@RequestBody CreateTeamRequest createTeamRequest,
                                    @AuthenticationPrincipal SecurityMember securityMember)
    {
        String leaderId = securityMember.getUsername();
        TeamCreateDto teamReq = createTeamRequest.toServiceDto();
        Long teamId = teamService.createTeam(leaderId, teamReq);
        return ApiResponse.ok(teamId);
    }
}
