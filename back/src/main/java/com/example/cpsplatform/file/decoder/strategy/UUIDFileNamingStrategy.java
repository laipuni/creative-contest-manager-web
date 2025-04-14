package com.example.cpsplatform.file.decoder.strategy;

import com.example.cpsplatform.file.domain.FileExtension;
import org.springframework.stereotype.Component;

import java.util.UUID;

public class UUIDFileNamingStrategy implements FileNamingStrategy{

    public static final String UUID_FORMAT = "%s.%s"; //ex) uuid.pdf

    @Override
    public String generate(final String originalFilename, final FileExtension extension) {
        return String.format(UUID_FORMAT, UUID.randomUUID(),extension);
    }
}
