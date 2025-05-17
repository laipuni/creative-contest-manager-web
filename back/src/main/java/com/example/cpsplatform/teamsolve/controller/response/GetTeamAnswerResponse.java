package com.example.cpsplatform.teamsolve.controller.response;

import com.example.cpsplatform.team.domain.SubmitStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetTeamAnswerResponse {

    private int finalSubmitCount;
    private SubmitStatus status;
    private List<GetTeamAnswerDto> teamAnswerList;

}
