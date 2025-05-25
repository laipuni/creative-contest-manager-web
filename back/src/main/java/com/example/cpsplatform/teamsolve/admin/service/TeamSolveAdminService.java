package com.example.cpsplatform.teamsolve.admin.service;

import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveDetailResponse;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListResponse;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamSolveAdminService {

    public static final String TEAM_SOLVE_ADMIN_LOG = "[TEAM_SOLVE_ADMIN]";

    private final TeamSolveRepository teamSolveRepository;
    private final FileRepository fileRepository;

    public TeamSolveListResponse getTeamSolveByTeam(final Long teamId, final TeamSolveType teamSolveType) {
        return teamSolveRepository.findTeamSolveByAdminCond(teamId,teamSolveType);
    }

    public TeamSolveDetailResponse getTeamSolveDetail(final Long teamId, final Long teamSolveId) {
        log.info("{} 팀(id:{})의 답안지(id:{})를 조회합니다.",TEAM_SOLVE_ADMIN_LOG,teamId,teamSolveId);
        TeamSolve teamSolve = teamSolveRepository.findById(teamSolveId)
                .orElseThrow(() -> new IllegalArgumentException("해당 답안지를 존재하지 않습니다."));
        Optional<File> optionalFile = fileRepository.findFileByTeamSolveId(teamSolveId);
        if(optionalFile.isPresent()){
            File file = optionalFile.get();
            log.info("{} 답안지(id:{})의 파일(id:{})도 함께 조회합니다.",TEAM_SOLVE_ADMIN_LOG,teamSolve.getId(),file.getId());
            return TeamSolveDetailResponse.of(teamSolve, file);
        }
        return TeamSolveDetailResponse.of(teamSolve);
    }
}
