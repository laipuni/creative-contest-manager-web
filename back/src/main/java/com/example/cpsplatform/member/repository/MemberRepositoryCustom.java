package com.example.cpsplatform.member.repository;

import com.example.cpsplatform.member.admin.controller.response.MemberInfoListResponse;
import com.example.cpsplatform.member.repository.dto.AdminMemberSearchCond;

public interface MemberRepositoryCustom {

    public MemberInfoListResponse searchMemberByAdminCond(AdminMemberSearchCond cond);

}
