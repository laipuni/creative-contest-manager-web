package com.example.cpsplatform.file.decoder;

import com.example.cpsplatform.exception.FileReadException;
import com.example.cpsplatform.exception.UnsupportedFileTypeException;
import com.example.cpsplatform.file.decoder.strategy.FileNamingStrategy;
import com.example.cpsplatform.file.decoder.strategy.UUIDFileNamingStrategy;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MultipartDecoder implements FileDecoder<List<MultipartFile>>{

    @Override
    public FileSources decode(final List<MultipartFile> files) {
        if(files == null || files.isEmpty()){
            //파일이 존재하지 않을 경우
            return FileSources.of(Collections.emptyList());
        }
        List<FileSource> fileSourceList = files.stream()
                .map(this::convertToFileSource).toList();
        return FileSources.of(fileSourceList);
    }

    private FileSource convertToFileSource(MultipartFile file) {
        String contentType = file.getContentType();
        FileNamingStrategy fileNamingStrategy = new UUIDFileNamingStrategy();
        FileExtension extension = getExtension(contentType);
        String originalFileName = validateOriginalFilename(file.getOriginalFilename());
        String uploadFileName = fileNamingStrategy.generate(file.getOriginalFilename(), extension);
        return new FileSource(
                uploadFileName,
                originalFileName,
                getBytes(file),
                contentType,
                extension,
                file.getSize()
        );
    }

    private static String validateOriginalFilename(final String originalFilename) {
        if (StringUtils.hasText(originalFilename)) {
            return originalFilename;
        }
        return "unknown";
    }

    private static FileExtension getExtension(String contentType){
        if (!StringUtils.hasText(contentType)) {
            throw new UnsupportedFileTypeException("해당 파일의 확장자는 지원하지 않습니다.");
        }
        return FileExtension.findFileExtensionByMimeType(contentType);
    }

    private static byte[] getBytes(final MultipartFile file){
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new FileReadException("파일의 읽는데 실패했습니다.",e);
        }
    }
}
