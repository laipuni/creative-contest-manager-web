package com.example.cpsplatform.problem.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.problem.controller.response.TeamProblemResponse;
import com.example.cpsplatform.problem.service.ProblemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/team/{teamId}")
    public ApiResponse<List<TeamProblemResponse>> getProblemsForTeam(@PathVariable Long teamId) {
        List<TeamProblemResponse> problems = problemService.getProblemsForTeam(teamId);
        return ApiResponse.ok(problems);
    }
}
