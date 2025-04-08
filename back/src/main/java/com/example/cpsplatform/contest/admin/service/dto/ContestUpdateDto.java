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

}
