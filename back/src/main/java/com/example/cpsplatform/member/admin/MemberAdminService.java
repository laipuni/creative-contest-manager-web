package com.example.cpsplatform.member.admin;

import com.example.cpsplatform.member.admin.controller.response.MemberInfoListResponse;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.repository.dto.AdminMemberSearchCond;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberAdminService {

    private final MemberRepository memberRepository;

    public MemberInfoListResponse searchMember(final AdminMemberSearchCond cond) {
        return memberRepository.searchMemberByAdminCond(cond);
    }
}
