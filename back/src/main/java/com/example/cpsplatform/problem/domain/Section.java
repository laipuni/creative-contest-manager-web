package com.example.cpsplatform.problem.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Section {

    COMMON("common","공통"),
    ELEMENTARY_MIDDLE("elementary_middle","초등-중등"),
    HIGH_NORMAL("high_normal","고등-일반");

    final String key;
    final String label;
    static final Map<String,Section> sectionMap = Arrays.stream(Section.values()).collect(
            Collectors.toMap(Section::getKey,section -> section)
    );

    @JsonFormat
    public static Section findLevelBy(final String key){
        return sectionMap.get(key.toLowerCase());
    }

}
