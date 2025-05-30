package com.example.cpsplatform.contest.admin.service.dto;

import com.example.cpsplatform.contest.Contest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ContestUpdateDto {

    private Long contestId;
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


    public ContestUpdateDto(final Long contestId, final String title, final int season, final String description, final LocalDateTime registrationStartAt, final LocalDateTime registrationEndAt, final LocalDateTime contestStartAt, final LocalDateTime contestEndAt) {
        this.contestId = contestId;
        this.title = title;
        this.season = season;
        this.description = description;
        this.registrationStartAt = registrationStartAt;
        this.registrationEndAt = registrationEndAt;
        this.contestStartAt = contestStartAt;
        this.contestEndAt = contestEndAt;
    }
}
