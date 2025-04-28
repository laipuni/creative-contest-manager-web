package com.example.cpsplatform.teamsolve.controller.request;


import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmitTeamAnswerRequest {

    @NotEmpty(message = "답안지를 제출할 문제들의 정보들은 필수입니다.")
    private List<Long> problemIds; //문제 id들
}
