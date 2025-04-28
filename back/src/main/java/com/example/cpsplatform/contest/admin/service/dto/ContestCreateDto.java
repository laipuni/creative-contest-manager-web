package com.example.cpsplatform.contest.admin.service.dto;

import com.example.cpsplatform.contest.Contest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    public Contest toEntity(){
        return Contest.of(title,description,season,registrationStartAt,
                registrationEndAt,contestStartAt,contestEndAt);
    }
}
