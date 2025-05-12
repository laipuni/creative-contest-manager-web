package com.example.cpsplatform.file.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum FileType {

    PROBLEM_REAL( "기출 문제"),
    PROBLEM_PRACTICE( "연습 문제"),
    TEAM_SOLUTION( "팀 답안"),
    NOTICE("공지 사항");

    final String description;
}
