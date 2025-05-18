package com.example.cpsplatform.teamsolve.admin.controller.response;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TeamSolveDetailResponse {

    private Long teamSolveId;
    private String problemTitle;
    private Section section;
    private Integer problemOrder;
    private TeamSolveType teamSolveType;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long fileId;
    private String fileName;

    public static TeamSolveDetailResponse of(TeamSolve teamSolve, File file){
        //답안지 파일이 존재할 경우 생성 메서드
        return TeamSolveDetailResponse.builder()
                .teamSolveId(teamSolve.getId())
                .problemTitle(teamSolve.getProblem().getTitle())
                .section(teamSolve.getProblem().getSection())
                .problemOrder(teamSolve.getProblem().getProblemOrder())
                .teamSolveType(teamSolve.getTeamSolveType())
                .content(teamSolve.getContent())
                .createdAt(teamSolve.getCreatedAt())
                .updatedAt(teamSolve.getUpdatedAt())
                .fileId(file.getId()) //파일 추가
                .fileName(file.getOriginalName())
                .build();
    }

    public static TeamSolveDetailResponse of(TeamSolve teamSolve){
        //답안지 파일이 존재하지 않는 경우 생성 메서드
        return TeamSolveDetailResponse.builder()
                .teamSolveId(teamSolve.getId())
                .problemTitle(teamSolve.getProblem().getTitle())
                .section(teamSolve.getProblem().getSection())
                .problemOrder(teamSolve.getProblem().getProblemOrder())
                .teamSolveType(teamSolve.getTeamSolveType())
                .content(teamSolve.getContent())
                .createdAt(teamSolve.getCreatedAt())
                .updatedAt(teamSolve.getUpdatedAt())
                .build();
    }
}
