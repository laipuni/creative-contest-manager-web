package com.example.cpsplatform.teamsolve.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetTeamAnswerResponse {

    private List<GetTeamAnswerDto> teamAnswerList;
}
