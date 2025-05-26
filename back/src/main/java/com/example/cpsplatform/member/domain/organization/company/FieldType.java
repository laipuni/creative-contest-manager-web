package com.example.cpsplatform.member.domain.organization.company;

import com.example.cpsplatform.file.domain.FileType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum FieldType {

    COMPUTER("컴퓨터","COMPUTER"),
    MEDIA("언론","MEDIA"),
    PUBLIC_SERVANT("공무원","PUBLIC_SERVANT"),
    MILITARY("군인","MILITARY"),
    SERVICE("서비스업","SERVICE"),
    ART("예술","ART"),
    ETC("기타","ETC");

    private final String description;
    private final String key;
    private static final Map<String, FieldType> fileTypeMap = Arrays.stream(FieldType.values()).collect(
            Collectors.toMap(FieldType::getDescription,fieldType -> fieldType)
    );

    private static final Map<String, FieldType> fileTypeKeyMap = Arrays.stream(FieldType.values()).collect(
            Collectors.toMap(FieldType::getKey,fieldType -> fieldType)
    );

    @JsonFormat
    public static FieldType findFiledType(final String key){
        if(StringUtils.hasText(key)){
            return fileTypeMap.get(key.toLowerCase());
        }
        return null;
    }

    public static FieldType findFiledTypeByKey(final String key){
        if(StringUtils.hasText(key)) {
            return fileTypeKeyMap.get(key.toUpperCase());
        }
        return null;
    }
}
