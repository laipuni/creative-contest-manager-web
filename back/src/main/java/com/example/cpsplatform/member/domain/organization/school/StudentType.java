package com.example.cpsplatform.member.domain.organization.school;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum StudentType {

    ELEMENTARY("초등학생","ELEMENTARY"),
    MIDDLE("중학생","MIDDLE"),
    HIGH("고등학생","HIGH"),
    COLLEGE("대학생","COLLEGE");

    final String description;
    final String key;
    final static Map<String, StudentType> studentTypeMap = Arrays.stream(StudentType.values()).collect(
            Collectors.toMap(StudentType::getDescription, value-> value)
    ); // 컴파일 단계에서 map을 만들어 description으로 상수 시간으로 enum값을 찾음
    final static Map<String, StudentType> studentTypeKeyMap = Arrays.stream(StudentType.values()).collect(
            Collectors.toMap(StudentType::getKey, value-> value)
    );


    //클라이언트 요청값을 enum으로 바꿀 때, 호출될 메소드
    @JsonFormat
    public static StudentType findStudentTypeBy(final String description){
        if(StringUtils.hasText(description)){
            return studentTypeMap.get(description);
        }
        return null;
    }

    public static StudentType findStudentTypeByKey(final String key){
        if(StringUtils.hasText(key)){
            return studentTypeKeyMap.get(key.toUpperCase());
        }
        return null;
    }

}
