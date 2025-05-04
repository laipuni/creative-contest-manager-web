package com.example.cpsplatform.teamsolve.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.file.decoder.MultipartDecoder;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.security.domain.SecurityMember;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerResponse;
import com.example.cpsplatform.teamsolve.controller.request.SubmitTeamAnswerRequest;
import com.example.cpsplatform.teamsolve.service.AnswerSubmitService;
import com.example.cpsplatform.teamsolve.service.dto.SubmitAnswerDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TeamSolveController {

    private final AnswerSubmitService answerSubmitService;

    @PostMapping(
            value = "/api/contests/{contestId}/team-solves",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE //multipart 미디어 타입일 경우
    )
    public ApiResponse<Object> submitTeamAnswers(
            @PathVariable("contestId") Long contestId,
            @Valid @RequestPart("request") SubmitTeamAnswerRequest request,
            @AuthenticationPrincipal SecurityMember securityMember,
            @RequestPart("file") List<MultipartFile> multipartFiles){

        if(multipartFiles.size() != request.getProblemIds().size()){
            //문제와 답안지 파일의 수가 일치하지 않을 경우 예외 발생
            log.debug("유저(loginId:{})가 제출한 답안지 파일({}개)과 문제 정보({}개)가 일치하지 않음",
                    securityMember.getUsername(), request.getProblemIds().size(),multipartFiles.size());
            throw new IllegalArgumentException("모든 문제의 파일을 제출 해주시길 바랍니다.");
        }

        MultipartDecoder multipartDecoder = new MultipartDecoder();
        FileSources fileSources = multipartDecoder.decode(multipartFiles);
        answerSubmitService.submitAnswer(
                fileSources,
                getAnswerDto(contestId, request, securityMember)
        );
        return ApiResponse.ok(null);
    }

    @GetMapping("/api/contest/{contestId}/team-solves")
    public ApiResponse<GetTeamAnswerResponse> getAnswerSubmissionRequest(@PathVariable("contestId")Long contestId,
                                                                         @AuthenticationPrincipal SecurityMember member){
        GetTeamAnswerResponse response = answerSubmitService.getAnswer(contestId, member.getUsername());
        return ApiResponse.ok(response);
    }


    private static SubmitAnswerDto getAnswerDto(final Long contestId, final SubmitTeamAnswerRequest request, final SecurityMember securityMember) {
        return SubmitAnswerDto.of(
                contestId,
                securityMember.getUsername(),
                LocalDateTime.now(),
                request.getProblemIds()
        );
    }


}
