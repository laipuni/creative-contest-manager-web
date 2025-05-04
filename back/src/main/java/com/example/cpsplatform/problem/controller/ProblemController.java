package com.example.cpsplatform.problem.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.problem.controller.response.TeamProblemResponse;
import com.example.cpsplatform.problem.service.ProblemService;
import com.example.cpsplatform.security.domain.SecurityMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/{contestId}/teams/{teamId}/problems/section")
    public ApiResponse<List<TeamProblemResponse>> getProblemsForTeam(@PathVariable Long teamId, @PathVariable Long contestId,
                                                                     @AuthenticationPrincipal SecurityMember securityMember) {
        List<TeamProblemResponse> problems = problemService.getProblemsForTeam(teamId, contestId, securityMember.getUsername());
        return ApiResponse.ok(problems);
    }
}
