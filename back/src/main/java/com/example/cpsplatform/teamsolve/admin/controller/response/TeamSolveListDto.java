package com.example.cpsplatform.teamsolve.admin.controller.response;

import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TeamSolveListDto {

    private Long problemId;
    private String problemName;
    private int problemOrder;
    private Section section;
    private Long teamSolveId;
    private TeamSolveType type;
    private LocalDateTime updatedAt;

}
