package com.example.cpsplatform.teamsolve.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.file.FileAccessService;
import com.example.cpsplatform.file.decoder.MultipartDecoder;
import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.queue.job.AnswerSubmitJob;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.team.service.TeamService;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerResponse;
import com.example.cpsplatform.teamsolve.controller.request.SubmitTeamAnswerRequest;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.service.AnswerSubmitService;
import com.example.cpsplatform.teamsolve.service.dto.FinalSubmitAnswerDto;
import com.example.cpsplatform.teamsolve.service.dto.SubmitAnswerDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TeamSolveController {

    private final AnswerSubmitService answerSubmitService;
    private final FileDownloadService fileDownloadService;
    private final FileAccessService fileAccessService;

    @PostMapping(value = "/api/contests/{contestId}/team-solves/complete")
    public ApiResponse<Object> submitTeamAnswersComplete(
            @PathVariable("contestId") Long contestId,
            @AuthenticationPrincipal SecurityMember securityMember){
        answerSubmitService.submitAnswerComplete(new FinalSubmitAnswerDto(LocalDateTime.now(), securityMember.getUsername(),contestId));
        return ApiResponse.ok(null);
    }

    @PostMapping(
            value = "/api/contests/{contestId}/team-solves",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE //multipart 미디어 타입일 경우
    )
    public ApiResponse<Object> submitAnswerTemporary(
            @PathVariable("contestId") Long contestId,
            @Valid @RequestPart("request") SubmitTeamAnswerRequest request,
            @AuthenticationPrincipal SecurityMember securityMember,
            @RequestPart(value = "file",required = false) MultipartFile multipartFile){

        MultipartDecoder multipartDecoder = new MultipartDecoder();
        FileSource fileSource = multipartDecoder.decode(multipartFile);
        answerSubmitService.submitAnswerTemporary(
                fileSource,
                getAnswerDto(contestId, request, securityMember)
        );
        return ApiResponse.ok(null);
    }

    @GetMapping("/api/contests/{contestId}/team-solves")
    public ApiResponse<GetTeamAnswerResponse> getAnswerSubmissionRequest(@PathVariable("contestId")Long contestId,
                                                                         @RequestParam(value = "submit_type", defaultValue = "") String submitType,
                                                                         @AuthenticationPrincipal SecurityMember member){
        GetTeamAnswerResponse response = answerSubmitService.getAnswer(
                contestId,
                member.getUsername(),
                TeamSolveType.findTeamSolveType(submitType)
        );
        return ApiResponse.ok(response);
    }

    @GetMapping("/api/teams/{teamId}/files/{fileId}/answer/download")
    public void downloadTeamAnswer(@PathVariable("teamId") Long teamId, @PathVariable("fileId") Long fileId,
                                   HttpServletResponse response, @AuthenticationPrincipal SecurityMember member){
        //해당 유저가 해당 파일을 다운로드 받을 권한이 있는지 체크
        fileAccessService.validateMemberFileAccess(teamId,fileId,member.getUsername());
        fileDownloadService.download(fileId,response);
    }


    private static SubmitAnswerDto getAnswerDto(final Long contestId, final SubmitTeamAnswerRequest request, final SecurityMember securityMember) {
        return SubmitAnswerDto.of(
                contestId,
                securityMember.getUsername(),
                LocalDateTime.now(),
                request.getProblemId(),
                request.getContents()
        );
    }


}
