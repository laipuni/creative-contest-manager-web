package com.example.cpsplatform.teamsolve.controller.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmitTeamAnswerRequest {

    @NotNull(message = "답안지를 제출할 문제들의 정보들은 필수입니다.")
    private Long problemId; //문제 id들

    @Size(max = 500, message = "답안의 본문은 최대 500자 이하입니다.")
    private String contents;
}
