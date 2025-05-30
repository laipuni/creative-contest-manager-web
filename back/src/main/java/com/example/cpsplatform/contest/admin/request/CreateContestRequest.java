package com.example.cpsplatform.contest.admin.request;

import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import com.example.cpsplatform.web.annotation.ContestValidDateOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ContestValidDateOrder()
public class CreateContestRequest {

    @NotBlank(message = "대회 제목은 필수입니다.")
    private String title;

    @Min(value = 1, message = "대회 연회는 양수여야 합니다.")
    private int season;

    private String description;

    @NotNull(message = "예선 접수 시작 시간은 필수입니다.")
    private LocalDateTime registrationStartAt;

    @NotNull(message = "예선 접수 마감 시간은 필수입니다.")
    private LocalDateTime registrationEndAt;

    @NotNull(message = "대회 시작 시간은 필수입니다.")
    private LocalDateTime contestStartAt;

    @NotNull(message = "대회 종료 시간은 필수입니다.")
    private LocalDateTime contestEndAt;

    // [임시 설계] 본선 대회 정보(title, 시간, 장소)를 현재 예선 테이블에 포함시킴
    private String finalContestTitle;

    private String finalContestLocation;

    private LocalDateTime finalContestStartTime;

    private LocalDateTime finalContestEndTime;

    public ContestCreateDto toContestCreateDto(){
        return new ContestCreateDto(title,season,description,registrationStartAt,
                registrationEndAt,contestStartAt,contestEndAt,
                finalContestTitle,finalContestLocation,finalContestStartTime,finalContestEndTime);
    }

}

