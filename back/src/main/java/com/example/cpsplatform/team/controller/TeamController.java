package com.example.cpsplatform.team.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.exception.DuplicateDataException;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.team.controller.request.CreateTeamRequest;
import com.example.cpsplatform.team.controller.request.DeleteTeamRequest;
import com.example.cpsplatform.team.controller.request.UpdateTeamRequest;
import com.example.cpsplatform.team.service.TeamService;
import com.example.cpsplatform.team.service.dto.MyTeamInfoByContestDto;
import com.example.cpsplatform.team.service.dto.MyTeamInfoDto;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/api/teams")
    public ApiResponse<Long> createTeam(@RequestBody @Valid CreateTeamRequest createTeamRequest,
                                    @AuthenticationPrincipal SecurityMember securityMember)
    {
        try{
            Long teamId = teamService.createTeam(
                    securityMember.getUsername(),
                    createTeamRequest.toServiceDto());
            return ApiResponse.ok(teamId);
        }catch (DataIntegrityViolationException e){
            throw new DuplicateDataException("중복된 회원이 존재합니다.");
        }
    }

    @PatchMapping("/api/teams/{teamId}")
    public ApiResponse<Void> updateTeam(@PathVariable Long teamId,
                                    @RequestBody @Valid UpdateTeamRequest updateTeamRequest,
                                    @AuthenticationPrincipal SecurityMember securityMember)
    {
        try{
            teamService.updateTeam(teamId, updateTeamRequest.toServiceDto(), securityMember.getUsername());
            return ApiResponse.ok(null);
        }catch (DataIntegrityViolationException e){
            throw new DuplicateDataException("중복된 회원이 존재합니다.");
        }
    }

    @DeleteMapping("/api/teams")
    public ApiResponse<Void> deleteTeam(@RequestBody @Valid DeleteTeamRequest deleteTeamRequest,
                                        @AuthenticationPrincipal SecurityMember securityMember) {
        teamService.deleteTeam(deleteTeamRequest.getTeamId(), securityMember.getUsername(), deleteTeamRequest.getContestId());
        return ApiResponse.ok(null);
    }

    @GetMapping("/api/teams/my-team")
    public ApiResponse<List<MyTeamInfoDto>> getMyTeam(@AuthenticationPrincipal SecurityMember securityMember){
        List<MyTeamInfoDto> myTeamInfo= teamService.getMyTeamInfo(securityMember.getUsername());
        return ApiResponse.ok(myTeamInfo);
    }

    @GetMapping("/api/contests/{contestId}/my-team")
    public ApiResponse<MyTeamInfoByContestDto> getMyTeamByContest(@AuthenticationPrincipal SecurityMember securityMember,
                                                                  @PathVariable Long contestId){
        MyTeamInfoByContestDto myTeamInfoByContestDto = teamService.getMyTeamInfoByContest(contestId, securityMember.getUsername());
        return ApiResponse.ok(myTeamInfoByContestDto);
    }

}
