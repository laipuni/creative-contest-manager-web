package com.example.cpsplatform.contest.admin.controller.response;

import com.example.cpsplatform.contest.Contest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class DeletedContestDto {

    private Long contestId;
    private String title;
    private int season;
    private LocalDateTime createdAt;

    public static DeletedContestDto of(Contest contest){
        return DeletedContestDto.builder()
                .contestId(contest.getId())
                .title(contest.getTitle())
                .season(contest.getSeason())
                .createdAt(contest.getCreatedAt())
                .build();
    }
}
