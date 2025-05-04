package com.example.cpsplatform.teamsolve.controller.response;

import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.problem.domain.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GetTeamAnswerDto {

    private Long teamSolveId;
    private String teamName;
    private Section section;
    private LocalDateTime updatedAt;
    private int modifyCount;
    private Long fileId;
    private String fileName;

    public GetTeamAnswerDto(final Long teamSolveId, final String teamName, final Section section, final LocalDateTime updatedAt, final int modifyCount) {
        this.teamSolveId = teamSolveId;
        this.teamName = teamName;
        this.section = section;
        this.updatedAt = updatedAt;
        this.modifyCount = modifyCount;
    }

    public void setFileInfo(File file){
        this.fileId = file.getId();
        this.fileName = file.getOriginalName();
    }
}
