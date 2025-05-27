package com.example.cpsplatform.team.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Division {

    ELEMENTARY("초등부","ELEMENTARY"),
    MIDDLE("중등부","MIDDLE"),
    HIGH("고등부","HIGH"),
    COLLEGE_GENERAL("대학일반부","COLLEGE_GENERAL");

    private final String description;
    private final String key;

}
