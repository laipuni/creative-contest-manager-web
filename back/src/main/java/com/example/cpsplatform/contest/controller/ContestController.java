package com.example.cpsplatform.contest.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.contest.controller.service.ContestJoinService;
import com.example.cpsplatform.security.domain.SecurityMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        contestJoinService.join(contestId,securityMember.getUsername(), LocalDateTime.now());
        return ApiResponse.ok(null);
    }


}
