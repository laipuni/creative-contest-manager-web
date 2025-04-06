package com.example.cpsplatform.member.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Gender {

    WOMAN("여자"),
    MAN("남자");

    final String description;
    final static Map<String,Gender> genderMap = Arrays.stream(Gender.values()).collect(
            Collectors.toMap(Gender::getDescription,value -> value)
    ); //컴파일 단계에서 map을 만들어 description으로 상수 시간으로 enum값을 찾음

    //클라이언트 요청값을 enum으로 바꿀 때, 호출될 메소드
    @JsonFormat
    public static Gender findGender(final String description){
        return genderMap.get(description);
    }
}
