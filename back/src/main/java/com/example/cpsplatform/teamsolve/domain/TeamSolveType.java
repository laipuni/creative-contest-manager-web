package com.example.cpsplatform.teamsolve.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum TeamSolveType {

    TEMP("temp","임시 제출"),
    SUBMITTED("submitted","최종 제출");

    private final String key;
    private final String label;
    private static final Map<String,TeamSolveType> solveTypeMap = Arrays.stream(TeamSolveType.values()).collect(
            Collectors.toMap(TeamSolveType::getLabel,teamSolveType -> teamSolveType)
    );

    public static TeamSolveType findTeamSolveType(String key){
        return solveTypeMap.get(key);
    }

}
