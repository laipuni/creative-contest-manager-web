package com.example.cpsplatform.problem.admin.controller.response;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContestProblemDetailResponse {

    private Long problemId;
    private String title;
    private int season;
    private Section section;
    private String content;
    private ProblemType problemType;
    private Integer problemOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ContestProblemFileDto> fileList;

    public static ContestProblemDetailResponse of(Problem problem, List<File> files){
        return ContestProblemDetailResponse.builder()
                .problemId(problem.getId())
                .title(problem.getTitle())
                .section(problem.getSection())
                .season(problem.getContest().getSeason())
                .content(problem.getContent())
                .problemType(problem.getProblemType())
                .problemOrder(problem.getProblemOrder())
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .fileList(
                        files.stream()
                        .map(ContestProblemFileDto::of)
                        .toList()
                )
                .build();
    }

}
