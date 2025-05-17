package com.example.cpsplatform.member.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.member.admin.MemberAdminService;
import com.example.cpsplatform.member.admin.controller.response.MemberDetailInfoResponse;
import com.example.cpsplatform.member.admin.controller.response.MemberInfoListResponse;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.repository.dto.AdminMemberSearchCond;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class MemberAdminController {

    private final MemberAdminService memberAdminService;

    @AdminLog
    @GetMapping("/api/admin/v1/members")
    public ApiResponse<MemberInfoListResponse> searchMember(@RequestParam(value = "page",defaultValue = "0") int page,
                                                            @RequestParam(value = "page_size",defaultValue = "10") int pageSize,
                                                            @RequestParam(value = "order",defaultValue = "desc") String order,
                                                            @RequestParam(value = "search",defaultValue = "") String search,
                                                            @RequestParam(value = "gender",required = false) String gender,
                                                            @RequestParam(value = "search_type",defaultValue = "") String searchType,
                                                            @RequestParam(value = "start_date",required = false) LocalDateTime startDate,
                                                            @RequestParam(value = "end_date",required = false) LocalDateTime endDate){
        MemberInfoListResponse response = memberAdminService.searchMember(
                AdminMemberSearchCond.of(
                        page,
                        pageSize,
                        order,
                        search,
                        searchType,
                        Gender.findGender(gender),
                        startDate,
                        endDate
                )
        );
        return ApiResponse.ok(response);
    }

    @AdminLog
    @GetMapping("/api/admin/members/{memberId}")
    public ApiResponse<MemberDetailInfoResponse> getMemberDetailInfo(@PathVariable("memberId") Long memberId){
        MemberDetailInfoResponse response = memberAdminService.getMemberDetailInfo(memberId);
        return ApiResponse.ok(response);
    }
}
