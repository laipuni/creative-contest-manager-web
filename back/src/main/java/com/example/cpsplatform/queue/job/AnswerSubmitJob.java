package com.example.cpsplatform.queue.job;

import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerSubmitJob {

    private TeamSolveType teamSolveType;
    private Long teamId;
    private Long problemId;
    private String path;
    private FileSource fileSource;
    private String content;

    public static AnswerSubmitJob of(TeamSolveType teamSolveType,Long teamId,Long problemId, FileSource fileSource, String path,String content){
        return AnswerSubmitJob.builder()
                .teamSolveType(teamSolveType)
                .teamId(teamId)
                .problemId(problemId)
                .fileSource(fileSource)
                .path(path)
                .content(content)
                .build();
    }

}
