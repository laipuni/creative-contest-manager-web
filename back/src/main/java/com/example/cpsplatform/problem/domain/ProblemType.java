package com.example.cpsplatform.problem.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProblemType {

    CONTEST("출제용"),
    PRACTICE("연습용");

    final String description;

    public static boolean isContestProblem(ProblemType problemType){
        return ProblemType.CONTEST.equals(problemType);
    }

}
