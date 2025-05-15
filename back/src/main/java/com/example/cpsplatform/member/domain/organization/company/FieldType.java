package com.example.cpsplatform.member.domain.organization.company;

import com.example.cpsplatform.file.domain.FileType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum FieldType {

    COMPUTER("컴퓨터"),
    MEDIA("언론"),
    PUBLIC_SERVANT("공무원"),
    MILITARY("군인"),
    SERVICE("서비스업"),
    ART("예술"),
    ETC("기타");

    private final String description;
    private static final Map<String, FieldType> fileTypeMap = Arrays.stream(FieldType.values()).collect(
            Collectors.toMap(FieldType::getDescription,fieldType -> fieldType)
    );

    @JsonFormat
    public static FieldType findFiledType(final String key){
        return fileTypeMap.get(key.toLowerCase());
    }
}
