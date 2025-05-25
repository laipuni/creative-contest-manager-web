package com.example.cpsplatform.teamsolve.controller.response;

import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GetTeamAnswerDto {

    private Long teamSolveId;
    private String content;
    private Long teamId;
    private String teamName;
    private Section section;
    private TeamSolveType teamSolveType;
    private LocalDateTime updatedAt;
    private Long fileId;
    private String fileName;

    public GetTeamAnswerDto(final Long teamSolveId,final String content, final Long teamId,
                            final String teamName, final Section section,
                            final TeamSolveType teamSolveType, final LocalDateTime updatedAt) {
        this.teamSolveId = teamSolveId;
        this.content = content;
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamSolveType = teamSolveType;
        this.section = section;
        this.updatedAt = updatedAt;
    }

    public void setFileInfo(File file){
        this.fileId = file.getId();
        this.fileName = file.getOriginalName();
    }
}
