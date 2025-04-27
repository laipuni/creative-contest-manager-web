package com.example.cpsplatform.file.decoder.vo;

import com.example.cpsplatform.file.decoder.strategy.FileNamingStrategy;
import com.example.cpsplatform.file.domain.FileExtension;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileSource implements Serializable {

    private String uploadFileName;
    private String originalFilename;
    private byte[] fileBytes;
    private String mimeType;
    private FileExtension extension;
    private long size;

}
