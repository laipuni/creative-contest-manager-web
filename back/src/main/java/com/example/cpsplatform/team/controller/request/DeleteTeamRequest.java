package com.example.cpsplatform.team.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteTeamRequest {

    @NotNull(message = "삭제할 팀의 정보는 필수입니다.")
    private Long teamId;

    @NotNull(message = "대회 정보는 필수입니다.")
    private Long contestId;
}
