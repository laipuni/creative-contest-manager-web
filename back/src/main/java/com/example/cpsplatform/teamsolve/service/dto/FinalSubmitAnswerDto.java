package com.example.cpsplatform.teamsolve.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FinalSubmitAnswerDto {

    private LocalDateTime now;
    private String loginId;
    private Long contestId;

    public static FinalSubmitAnswerDto of(final Long contestId, final String username,final LocalDateTime now) {
        return FinalSubmitAnswerDto.builder()
                .now(now)
                .loginId(username)
                .contestId(contestId)
                .build();
    }
}