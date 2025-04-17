package com.example.cpsplatform.file.decoder.strategy;

import com.example.cpsplatform.file.domain.FileExtension;

public interface FileNamingStrategy {
    String generate(String originalFilename, FileExtension extension);
}
