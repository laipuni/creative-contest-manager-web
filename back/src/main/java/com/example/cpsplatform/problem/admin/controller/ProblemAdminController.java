package com.example.cpsplatform.problem.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.file.decoder.MultipartDecoder;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.problem.admin.controller.request.AddContestProblemRequest;
import com.example.cpsplatform.problem.admin.service.ContestProblemAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin/problems")
@RequiredArgsConstructor
public class ProblemAdminController {

    private final ContestProblemAdminService contestProblemAdminService;

    @AdminLog
    @PostMapping("/real")
    public ApiResponse<Object> addContestProblem (
            @Valid @RequestPart AddContestProblemRequest request,
            @RequestPart(required = false) List<MultipartFile> multipartFiles){
        MultipartDecoder multipartDecoder = new MultipartDecoder();
        FileSources fileSource = multipartDecoder.decode(multipartFiles);
        contestProblemAdminService.addContestProblem(request.toAddProblemDto(),fileSource);
        return ApiResponse.ok(null);
    }

}
