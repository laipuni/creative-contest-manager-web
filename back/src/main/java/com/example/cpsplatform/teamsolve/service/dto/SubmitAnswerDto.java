package com.example.cpsplatform.teamsolve.service.dto;

import com.example.cpsplatform.file.decoder.vo.FileSources;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SubmitAnswerDto {

    private LocalDateTime now;
    private String loginId;
    private Long contestId;
    private List<Long> problemIds;

    public static SubmitAnswerDto of(final Long contestId,
                                     final String username,final LocalDateTime now,
                                     List<Long> problemIds) {
        return SubmitAnswerDto.builder()
                .now(now)
                .loginId(username)
                .contestId(contestId)
                .problemIds(problemIds)
                .build();
    }
}
