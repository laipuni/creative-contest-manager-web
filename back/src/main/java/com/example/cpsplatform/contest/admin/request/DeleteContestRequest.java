package com.example.cpsplatform.contest.admin.request;

import com.example.cpsplatform.contest.admin.service.dto.ContestDeleteDto;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteContestRequest {

    @NotNull(message = "삭제할 대회의 정보는 필수입니다.")
    private Long contestId;

    public ContestDeleteDto toContestDeleteDto(){
        return new ContestDeleteDto(contestId);
    }
}
