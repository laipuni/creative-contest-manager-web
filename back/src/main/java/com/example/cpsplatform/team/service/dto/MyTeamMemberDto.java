package com.example.cpsplatform.team.service.dto;

import com.example.cpsplatform.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyTeamMemberDto {

    private Long memberId;
    private String loginId;
    private String name;

    public static MyTeamMemberDto of(final Member member){
        return MyTeamMemberDto.builder()
                .memberId(member.getId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .build();
    }

}
