package com.example.cpsplatform.file.repository;

import com.example.cpsplatform.file.repository.dto.FileNameDto;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;

import java.util.List;

public interface FileRepositoryCustom {

    public List<FileNameDto> findFileNameDto(List<Long> fileIds);

    public List<Long> findFileIdsByContestIdInTeamSolve(Long contestId, TeamSolveType teamSolveType);

}
