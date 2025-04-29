package com.example.cpsplatform.file.controller;

import com.example.cpsplatform.contest.service.ContestJoinService;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.security.domain.SecurityMember;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ContestProblemFileController {

    private final FileDownloadService fileDownloadService;
    private final ContestJoinService contestJoinService;
    @GetMapping("/api/contests/{contestId}/files/{fileId}")
    public void downloadContestProblem(@PathVariable("contestId") Long contestId,
                                       @PathVariable("fileId") Long fileId,
                                       @AuthenticationPrincipal SecurityMember member,
                                       HttpServletResponse response){
        //대회 참가한 유저이고, 현재 대회 시간인지 검증
        contestJoinService.validateContestParticipation(contestId, member.getUsername(), LocalDateTime.now());
        //파일 다운로드
        fileDownloadService.download(fileId,response);
    }

}
