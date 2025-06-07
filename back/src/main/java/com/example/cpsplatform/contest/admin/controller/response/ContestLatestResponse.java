package com.example.cpsplatform.contest.admin.controller.response;

import com.example.cpsplatform.contest.Contest;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long finalContestId;
    private String finalContestTitle;
    private String location;
    private LocalDateTime finalContestStartTime;
    private LocalDateTime finalContestEndTime;

    public static ContestLatestResponse of(Contest contest){
        return ContestLatestResponse.builder()
                .contestId(contest.getId())
                .season(contest.getSeason())
                .title(contest.getTitle())
                .registrationStartAt(contest.getRegistrationStartAt())
                .registrationEndAt(contest.getRegistrationEndAt())
                .startTime(contest.getStartTime())
                .endTime(contest.getEndTime())
                .finalContestId(contest.getFinalContest().getId())
                .finalContestTitle(contest.getFinalContest().getTitle())
                .finalContestStartTime(contest.getFinalContest().getStartTime())
                .finalContestEndTime(contest.getFinalContest().getEndTime())
                .build();
    }

}
