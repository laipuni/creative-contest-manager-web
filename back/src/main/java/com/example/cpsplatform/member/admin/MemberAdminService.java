package com.example.cpsplatform.member.admin;

import com.example.cpsplatform.member.admin.controller.response.MemberDetailInfoResponse;
import com.example.cpsplatform.member.admin.controller.response.MemberInfoListResponse;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.member.repository.dto.AdminMemberSearchCond;
import com.example.cpsplatform.security.encoder.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberAdminService {

    public static final String MEMBER_ADMIN_LOG = "[MemberAdminService]";

    private final MemberRepository memberRepository;
    private final CryptoService cryptoService;

    public MemberInfoListResponse searchMember(final AdminMemberSearchCond cond) {
        return memberRepository.searchMemberByAdminCond(cond);
    }

    public MemberDetailInfoResponse getMemberDetailInfo(final Long memberId) {
        log.info("{} 유저(id:{})의 정보를 조회",MEMBER_ADMIN_LOG,memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));
        return MemberDetailInfoResponse.of(member,cryptoService);
    }
}
