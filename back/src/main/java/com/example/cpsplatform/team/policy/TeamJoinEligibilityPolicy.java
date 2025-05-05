package com.example.cpsplatform.team.policy;

import com.example.cpsplatform.member.domain.Member;


/**
 * 만약 매년 회원가입 방식이 아닌 다른 정책으로 바뀌었을 경우,
 * 정책 구현체를 구현해서 PolicyConfig에서 새로운 정책 구현체로 바꾸면 된다.
 */
public interface TeamJoinEligibilityPolicy {

    public void validate(Member member);

}
