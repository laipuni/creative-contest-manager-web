package com.example.cpsplatform.team.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubmitStatus {

    NOT_SUBMITTED("미제출"), //아직 한 번도 제출하지 않음
    TEMPORARY("임시 제출"),
    FINAL("최종 제출");

    private final String label;
}
