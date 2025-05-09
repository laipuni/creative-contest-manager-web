package com.example.cpsplatform.contest.controller.response;

import com.example.cpsplatform.contest.Contest;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class LatestContestResponse {

    private Long contestId;
    private int season;
    private LocalDateTime registrationStartAt;
    private LocalDateTime registrationEndAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static LatestContestResponse of(Contest contest){
        return LatestContestResponse.builder()
                .contestId(contest.getId())
                .season(contest.getSeason())
                .registrationStartAt(contest.getRegistrationStartAt())
                .registrationEndAt(contest.getRegistrationEndAt())
                .startTime(contest.getStartTime())
                .endTime(contest.getEndTime())
                .build();
    }

}
