package com.example.cpsplatform.notice.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.notice.admin.controller.response.NoticeSearchResponse;
import com.example.cpsplatform.notice.controller.response.UserNoticeSearchResponse;
import com.example.cpsplatform.notice.repository.dto.AdminSearchNoticeCond;
import com.example.cpsplatform.notice.repository.dto.UserSearchNoticeCond;
import com.example.cpsplatform.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/api/notices/search")
    public ApiResponse<UserNoticeSearchResponse> searchNotices(@RequestParam(value = "page",defaultValue = "0") int page,
                                                               @RequestParam(value = "page_size",defaultValue = "10") int pageSize,
                                                               @RequestParam(value = "keyword",defaultValue = "") String keyword,
                                                               @RequestParam(value = "search_type",defaultValue = "") String searchType,
                                                               @RequestParam(value = "order",defaultValue = "desc") String order,
                                                               @RequestParam(value = "order_type",defaultValue = "createdAt") String orderType){
        UserNoticeSearchResponse response = noticeService.searchNotice(
                UserSearchNoticeCond.of(page, pageSize, keyword, searchType, order, orderType)
        );
        return ApiResponse.ok(response);
    }

}
