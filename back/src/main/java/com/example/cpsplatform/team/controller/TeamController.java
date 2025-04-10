package com.example.cpsplatform.team.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.team.controller.request.CreateTeamRequest;
import com.example.cpsplatform.team.controller.request.UpdateTeamRequest;
import com.example.cpsplatform.team.service.TeamService;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/api/team")
    public ApiResponse<Long> createTeam(@RequestBody @Valid CreateTeamRequest createTeamRequest,
                                    @AuthenticationPrincipal SecurityMember securityMember)
    {
        Long teamId = teamService.createTeam(
                securityMember.getUsername(),
                createTeamRequest.toServiceDto());
        return ApiResponse.ok(teamId);
    }

    @PatchMapping("/api/team/{teamId}")
    public ApiResponse<Void> updateTeam(@PathVariable Long teamId,
                                    @RequestBody @Valid UpdateTeamRequest updateTeamRequest,
                                    @AuthenticationPrincipal SecurityMember securityMember)
    {
        teamService.updateTeam(teamId, updateTeamRequest.toServiceDto(), securityMember.getUsername());
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{teamId}")
    public ApiResponse<Void> deleteTeam(@PathVariable Long teamId,
                                        @AuthenticationPrincipal SecurityMember principal) {
        teamService.deleteTeam(teamId, principal.getUsername());
        return ApiResponse.ok(null);
    }
}
