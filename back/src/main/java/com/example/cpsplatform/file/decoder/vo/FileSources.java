package com.example.cpsplatform.file.decoder.vo;

import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.service.dto.FileSaveDto;
import com.example.cpsplatform.file.storage.dto.FileUploadInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
public class FileSources {

    private List<FileSource> fileSourceList;

    public static FileSources of(List<FileSource> fileSourceList){
        return FileSources.builder()
                .fileSourceList(fileSourceList)
                .build();
    }

    public int getSize(){
        return fileSourceList.size();
    }

    public List<FileSource> getFileSourceList(){
        return Collections.unmodifiableList(fileSourceList);
    }


    public List<FileSaveDto> toFileSaveDtos(String path, FileType fileType) {
        return fileSourceList.stream()
                .map(file -> new FileSaveDto(
                        file.getUploadFileName(),
                        file.getOriginalFilename(),
                        file.getExtension(),
                        file.getMimeType(),
                        file.getSize(),
                        path,
                        fileType
                ))
                .toList();
    }

}
