package com.example.cpsplatform.file.domain;

import com.example.cpsplatform.exception.UnsupportedFileTypeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum FileExtension {

    PDF("pdf", "application/pdf");

    final String extension;
    final String mimeType;
    private static final Map<String, FileExtension> extensionMap = Arrays.stream(FileExtension.values()).collect(
            Collectors.toMap(FileExtension::getExtension,fileExtension -> fileExtension)
    );
    private static final Map<String, FileExtension> mimeTypeMap = Arrays.stream(FileExtension.values()).collect(
            Collectors.toMap(FileExtension::getMimeType,fileExtension -> fileExtension)
    );

    public static FileExtension findFileExtensionByExtension(final String extension){
        return Optional.ofNullable(extensionMap.get(extension))
                .orElseThrow(() -> new UnsupportedFileTypeException("지원하지 않는 파일 확장자입니다." + extension));
    }

    public static FileExtension findFileExtensionByMimeType(final String mimeType){
        return Optional.ofNullable(mimeTypeMap.get(mimeType))
                .orElseThrow(() -> new UnsupportedFileTypeException("지원하지 않는 파일 타입입니다." + mimeType));
    }
}
