package com.example.cpsplatform.contest.admin.controller.response;

import com.example.cpsplatform.contest.Contest;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ContestLatestResponse {

    private Long contestId;
    private int season;
    private String title;
    private LocalDateTime registrationStartAt;
    private LocalDateTime registrationEndAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static ContestLatestResponse of(Contest contest){
        return ContestLatestResponse.builder()
                .contestId(contest.getId())
                .season(contest.getSeason())
                .title(contest.getTitle())
                .registrationStartAt(contest.getRegistrationStartAt())
                .registrationEndAt(contest.getRegistrationEndAt())
                .startTime(contest.getStartTime())
                .endTime(contest.getEndTime())
                .build();
    }

}
