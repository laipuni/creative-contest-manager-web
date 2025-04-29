package com.example.cpsplatform.contest.admin.controller.response;

import com.example.cpsplatform.contest.Contest;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContestListDto {

    private Long contestId;
    private String title;
    private int season;
    private LocalDateTime registrationStartAt;
    private LocalDateTime registrationEndAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static ContestListDto of(Contest contest){
        return ContestListDto.builder()
                .contestId(contest.getId())
                .title(contest.getTitle())
                .season(contest.getSeason())
                .registrationStartAt(contest.getRegistrationStartAt())
                .registrationEndAt(contest.getRegistrationEndAt())
                .startTime(contest.getStartTime())
                .endTime(contest.getEndTime())
                .build();
    }
}
