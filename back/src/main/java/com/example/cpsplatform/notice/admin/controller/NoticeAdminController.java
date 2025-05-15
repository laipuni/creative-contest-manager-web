package com.example.cpsplatform.notice.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.file.decoder.MultipartDecoder;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.notice.admin.controller.request.NoticeAddRequest;
import com.example.cpsplatform.notice.admin.controller.request.NoticeModifyRequest;
import com.example.cpsplatform.notice.admin.controller.response.NoticeAddResponse;
import com.example.cpsplatform.notice.admin.controller.response.NoticeModifyResponse;
import com.example.cpsplatform.notice.admin.service.NoticeFacadeService;
import com.example.cpsplatform.security.domain.SecurityMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeFacadeService noticeFacadeService;

    @AdminLog
    @PostMapping("/api/admin/notices")
    public ApiResponse<NoticeAddResponse> addNotice(@Valid @RequestPart NoticeAddRequest request,
                                         @AuthenticationPrincipal SecurityMember member,
                                         @RequestPart List<MultipartFile> files){
        MultipartDecoder multipartDecoder = new MultipartDecoder();
        FileSources fileSources = multipartDecoder.decode(files);
        NoticeAddResponse response = noticeFacadeService.publishNotice(request.getTitle(), request.getContent(), member.getUsername(), fileSources);
        return ApiResponse.ok(response);
    }

    @AdminLog
    @PatchMapping("/api/admin/notices")
    public ApiResponse<NoticeModifyResponse> modifyNotice(@Valid @RequestPart NoticeModifyRequest request,
                                                       @AuthenticationPrincipal SecurityMember member,
                                                       @RequestPart List<MultipartFile> files){
        MultipartDecoder multipartDecoder = new MultipartDecoder();
        FileSources fileSources = multipartDecoder.decode(files);

        NoticeModifyResponse response = noticeFacadeService.modifyNotice(request.toNoticeModifyDto(member.getUsername()), fileSources);
        return ApiResponse.ok(response);
    }
}
