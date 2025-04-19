package com.example.cpsplatform.problem.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.file.decoder.MultipartDecoder;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.problem.admin.controller.request.AddContestProblemRequest;
import com.example.cpsplatform.problem.admin.controller.request.UpdateContestProblemRequest;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemDetailResponse;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemListResponse;
import com.example.cpsplatform.problem.admin.service.ContestProblemAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @AdminLog
    @GetMapping("v1/contests/{contestId}/problems/{problemId}")
    public ApiResponse<ContestProblemDetailResponse> getContestProblemDetail(
            @PathVariable("contestId") Long contestId,
            @PathVariable("problemId") Long problemId){
        ContestProblemDetailResponse response = contestProblemAdminService.findContestProblemDetail(contestId,problemId);
        return ApiResponse.ok(response);
    }

    @AdminLog
    @PutMapping("/contests/{contestId}/problems/{problemId}")
    public ApiResponse<Object> updateContestProblem (
            @PathVariable("contestId") Long contestId,
            @PathVariable("problemId") Long problemId,
            @Valid @RequestPart UpdateContestProblemRequest request,
            @RequestPart(required = false) List<MultipartFile> multipartFiles){
        MultipartDecoder multipartDecoder = new MultipartDecoder();
        FileSources fileSource = multipartDecoder.decode(multipartFiles);
        contestProblemAdminService.updateContestProblem(
                request.toUpdateProblemDto(contestId,problemId),
                fileSource
        );
        return ApiResponse.ok(null);
    }

}
