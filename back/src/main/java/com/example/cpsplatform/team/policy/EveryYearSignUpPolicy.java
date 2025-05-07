package com.example.cpsplatform.team.policy;

import com.example.cpsplatform.exception.TeamJoinPolicyViolationException;
import com.example.cpsplatform.member.domain.Member;
import lombok.extern.slf4j.Slf4j;

/**
 * 매년 새로운 회원가입으로 팀을 참여해야하는 정책(대표님의 요구사항)
 */
@Slf4j
public class EveryYearSignUpPolicy implements TeamJoinEligibilityPolicy{

    @Override
    public void validate(final Member member) {
        log.info("팀 가입 정책 위반 - 회원ID: {}, 가입연도: {}", member.getLoginId(), member.getCreatedAt().getYear());
        if (!member.isSignedUpThisYear()) {
            throw new TeamJoinPolicyViolationException(String.format(
                    "%s님은 해당 연도에 가입자가 아닙니다. 새롭게 회원 가입을 해주시길 바랍니다.",
                    member.getLoginId())
            );
        }
    }
}
