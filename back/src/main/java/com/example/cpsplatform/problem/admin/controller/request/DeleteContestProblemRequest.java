package com.example.cpsplatform.problem.admin.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteContestProblemRequest {

    @NotNull(message = "삭제할 문제의 정보는 필수입니다.")
    private Long deleteProblemId;

}
