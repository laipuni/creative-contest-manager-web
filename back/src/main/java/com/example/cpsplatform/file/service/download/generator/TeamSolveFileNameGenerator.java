package com.example.cpsplatform.file.service.download.generator;

import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.repository.dto.FileNameDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamSolveFileNameGenerator implements DownloadFileNameGenerator{

    public static final String TEAM_SOLVE_FORMAT = "%d회_%s_%s_%d번_답안지.%s"; // ex) 16회_공통_xx팀_1번_답안지.pdf

    private final FileRepository fileRepository;

    public TeamSolveFileNameGenerator(final FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public Map<Long,String> generate(final List<Long> fileIds) {
        List<FileNameDto> fileNameDto = fileRepository.findFileNameDto(fileIds);
        return fileNameDto.stream().collect(
                Collectors.toMap(FileNameDto::getFileId, this::generateFileName)
        );
    }

    private String generateFileName(FileNameDto fileNameDto){
        return String.format(TEAM_SOLVE_FORMAT,
                    fileNameDto.getSeason(),
                    fileNameDto.getSection().getLabel(),
                    fileNameDto.getTeamName(),
                    fileNameDto.getProblemOrder(),
                    fileNameDto.getFileExtension().getExtension()
                );
    }
}
