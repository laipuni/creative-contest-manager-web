package com.example.cpsplatform.contest.admin.service.dto;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.finalcontest.FinalContest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ContestCreateDto {

    private String title;
    private int season;
    private String description;
    private LocalDateTime registrationStartAt;
    private LocalDateTime registrationEndAt;
    private LocalDateTime contestStartAt;
    private LocalDateTime contestEndAt;
    private String finalContestTitle;
    private String finalContestLocation;
    private LocalDateTime finalContestStartTime;
    private LocalDateTime finalContestEndTime;

    public ContestCreateDto(final String title, final int season, final String description, final LocalDateTime registrationStartAt, final LocalDateTime registrationEndAt, final LocalDateTime contestStartAt, final LocalDateTime contestEndAt) {
        this.title = title;
        this.season = season;
        this.description = description;
        this.registrationStartAt = registrationStartAt;
        this.registrationEndAt = registrationEndAt;
        this.contestStartAt = contestStartAt;
        this.contestEndAt = contestEndAt;
    }

    public Contest toEntity(){
        return Contest.of(title,description,season,registrationStartAt,
                registrationEndAt,contestStartAt,contestEndAt, toFinalContestEntity());
    }

    public FinalContest toFinalContestEntity(){
        return FinalContest.of(finalContestTitle,finalContestLocation,finalContestStartTime,finalContestEndTime);
    }
}
