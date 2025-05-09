package com.example.cpsplatform.exception;

/**
 * 팀을 가입할 때, 정책을 준수하지 않는 유저일 경우 발생한다.
 * 1. 해당 연도 가입자가 아닐 경우
 * 2. 해당 연도에 개인정보를 갱신하지 않았을 경우
 */
public class TeamJoinPolicyViolationException extends RuntimeException {

    public TeamJoinPolicyViolationException(final String message) {
        super(message);
    }

    public TeamJoinPolicyViolationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
