package com.example.cpsplatform.file.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.service.dto.FileSaveDto;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public List<Long> getTeamSolveFileIdsByContestId(final Long contestId, final TeamSolveType teamSolveType) {
        return fileRepository.findFileIdsByContestIdInTeamSolve(contestId,teamSolveType);
    }
}
