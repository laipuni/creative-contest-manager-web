package com.example.cpsplatform.contest.admin.controller.response;

import com.example.cpsplatform.contest.Contest;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContestDetailResponse {

    private Long contestId;
    private String title;
    private String description;
    private int season;
    private LocalDateTime registrationStartAt;
    private LocalDateTime registrationEndAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ContestDetailResponse of(Contest contest){
        return ContestDetailResponse.builder()
                .title(contest.getTitle())
                .description(contest.getDescription())
                .season(contest.getSeason())
                .registrationStartAt(contest.getRegistrationStartAt())
                .registrationEndAt(contest.getRegistrationEndAt())
                .startTime(contest.getStartTime())
                .endTime(contest.getEndTime())
                .createdAt(contest.getCreatedAt())
                .updatedAt(contest.getUpdatedAt())
                .build();
    }
}
