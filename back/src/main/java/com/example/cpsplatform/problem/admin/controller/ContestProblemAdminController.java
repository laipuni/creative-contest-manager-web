package com.example.cpsplatform.problem.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemListResponse;
import com.example.cpsplatform.problem.admin.service.ContestProblemAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class ContestProblemAdminController {

    private final ContestProblemAdminService contestProblemAdminService;

    @AdminLog
    @GetMapping("v1/contests/{contestId}/problems")
    public ApiResponse<ContestProblemListResponse> getContestProblemList(
            @PathVariable("contestId") Long contestId,
            @RequestParam(value = "page",defaultValue = "0") int page){
        ContestProblemListResponse result = contestProblemAdminService.findContestProblemList(contestId, page);
        return ApiResponse.ok(result);
    }

}
