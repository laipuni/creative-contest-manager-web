package com.example.cpsplatform.file.repository;

import com.example.cpsplatform.file.repository.dto.FileNameDto;

import java.util.List;

public interface FileRepositoryCustom {

    public List<FileNameDto> findFileNameDto(List<Long> fileIds);
}
