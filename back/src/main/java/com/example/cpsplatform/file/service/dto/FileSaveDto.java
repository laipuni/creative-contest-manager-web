package com.example.cpsplatform.file.service.dto;

import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.problem.domain.Problem;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileSaveDto {

    private String name;
    private String originalName;
    private FileExtension extension;
    private String mimeType;
    private Long size;
    private String path;
    private FileType fileType;

    public File createContestProblemFile(Problem problem) {
        return File.createContestProblemFile(
                name,
                originalName,
                extension,
                mimeType,
                size,
                path,
                fileType,
                problem
        );
    }
}
