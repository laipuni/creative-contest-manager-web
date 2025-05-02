package com.example.cpsplatform.problem.controller.response;

import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemFileDto;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TeamProblemResponse {
    private Long problemId;
    private String title;
    private String content;
    private Section problemType;
    private List<ContestProblemFileDto> fileList;

    public static TeamProblemResponse of(Problem problem) {
        return TeamProblemResponse.builder()
                .problemId(problem.getId())
                .title(problem.getTitle())
                .content(problem.getContent())
                .problemType(problem.getSection())
                .fileList(
                        problem.getFiles().stream()
                                .map(ContestProblemFileDto::of)
                                .toList()
                )
                .build();
    }
}
