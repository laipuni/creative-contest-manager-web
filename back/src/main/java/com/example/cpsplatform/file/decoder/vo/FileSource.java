package com.example.cpsplatform.file.decoder.vo;

import com.example.cpsplatform.file.decoder.strategy.FileNamingStrategy;
import com.example.cpsplatform.file.domain.FileExtension;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileSource {

    private String uploadFileName;
    private String originalFilename;
    private byte[] fileBytes;
    private String mimeType;
    private FileExtension extension;
    private long size;

}
