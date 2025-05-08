package com.example.cpsplatform.team.policy;

import com.example.cpsplatform.exception.TeamJoinPolicyViolationException;
import com.example.cpsplatform.member.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * 매년 새로운 회원가입으로 팀을 참여해야하는 정책(대표님의 요구사항)
 */
@Slf4j
public class EveryYearSignUpPolicy implements TeamJoinEligibilityPolicy{

    @Override
    public void validate(final Member member) {
        int year = LocalDate.now().getYear();
        log.info("팀 가입 정책 (매년 회원가입 정책) - 회원ID: {}, 현재 연도: {}", member.getLoginId(), year);
        if (!member.isSignedUpThisYear(year)) {
            log.info("팀 가입 정책 (매년 회원가입 정책) 위반 - 회원ID: {}, 현재 연도: {}, 가입연도: {}", member.getLoginId(),year, member.getCreatedAt().getYear());
            throw new TeamJoinPolicyViolationException(String.format(
                    "%s님은 해당 연도에 가입자가 아닙니다. 새롭게 회원 가입을 해주시길 바랍니다.",
                    member.getLoginId())
            );
        }
    }
}
