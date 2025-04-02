package com.example.cpsplatform.member.domain.organization.school;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum StudentType {

    ELEMENTARY("초등학생"),
    MIDDLE("중학생"),
    HIGH("고등학생"),
    COLLEGE("대학생");

    final String description;
    final static Map<String, StudentType> studentTypeMap = Arrays.stream(StudentType.values()).collect(
            Collectors.toMap(StudentType::getDescription, value-> value)
    ); // 컴파일 단계에서 map을 만들어 description으로 상수 시간으로 enum값을 찾음

    //클라이언트 요청값을 enum으로 바꿀 때, 호출될 메소드
    @JsonFormat
    public static StudentType findStudentTypeBy(final String description){
        return studentTypeMap.get(description);
    }

}
