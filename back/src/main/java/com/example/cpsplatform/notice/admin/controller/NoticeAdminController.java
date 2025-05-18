package com.example.cpsplatform.notice.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.file.decoder.MultipartDecoder;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.notice.admin.controller.response.NoticeDetailResponse;
import com.example.cpsplatform.notice.admin.controller.response.NoticeSearchResponse;
import com.example.cpsplatform.notice.admin.controller.request.NoticeAddRequest;
import com.example.cpsplatform.notice.admin.controller.request.NoticeDeleteRequest;
import com.example.cpsplatform.notice.admin.controller.request.NoticeModifyRequest;
import com.example.cpsplatform.notice.admin.controller.response.NoticeAddResponse;
import com.example.cpsplatform.notice.admin.controller.response.NoticeModifyResponse;
import com.example.cpsplatform.notice.admin.service.NoticeAdminService;
import com.example.cpsplatform.notice.admin.service.NoticeAdminFacadeService;
import com.example.cpsplatform.notice.repository.dto.AdminSearchNoticeCond;
import com.example.cpsplatform.security.domain.SecurityMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeAdminFacadeService noticeFacadeService;
    private final NoticeAdminService noticeAdminService;

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

    @AdminLog
    @DeleteMapping("/api/admin/notices")
    public ApiResponse<Object> deleteNotice(@Valid @RequestPart NoticeDeleteRequest request){
        noticeFacadeService.deleteNotice(request.getNoticeId());
        return ApiResponse.ok(null);
    }

    @AdminLog
    @GetMapping("/api/admin/notices/search")
    public ApiResponse<NoticeSearchResponse> searchNotices(@RequestParam(value = "page",defaultValue = "0") int page,
                                                           @RequestParam(value = "page_size",defaultValue = "10") int pageSize,
                                                           @RequestParam(value = "keyword",defaultValue = "") String keyword,
                                                           @RequestParam(value = "search_type",defaultValue = "") String searchType,
                                                           @RequestParam(value = "order",defaultValue = "desc") String order,
                                                           @RequestParam(value = "order_type",defaultValue = "createdAt") String orderType){
        NoticeSearchResponse response = noticeAdminService.searchNotice(
                AdminSearchNoticeCond.of(page, pageSize, keyword, searchType, order, orderType)
        );
        return ApiResponse.ok(response);
    }

    @AdminLog
    @GetMapping("/api/admin/notices/{noticeId}")
    public ApiResponse<NoticeDetailResponse> getNoticeDetail(@PathVariable("noticeId") Long noticeId){
        NoticeDetailResponse response = noticeFacadeService.getNoticeDetail(noticeId);
        return ApiResponse.ok(response);
    }

}
