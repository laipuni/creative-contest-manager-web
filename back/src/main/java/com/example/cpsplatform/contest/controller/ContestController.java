package com.example.cpsplatform.contest.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.contest.controller.response.LatestContestResponse;
import com.example.cpsplatform.contest.service.ContestJoinService;
import com.example.cpsplatform.security.domain.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contests/")
public class ContestController {

    private final ContestJoinService contestJoinService;

    @PostMapping("/{contestId}/join")
    public ApiResponse<Object> joinContest(
            @PathVariable("contestId") Long contestId,
            @AuthenticationPrincipal SecurityMember securityMember
            ){
        contestJoinService.validateContestParticipation(contestId,securityMember.getUsername(), LocalDateTime.now());
        return ApiResponse.ok(null);
    }

    @GetMapping("/latest")
    public ApiResponse<LatestContestResponse> getLatestContestInfo(){
        LatestContestResponse response = contestJoinService.getLatestContestInfo();
        return ApiResponse.ok(response);
    }
}
