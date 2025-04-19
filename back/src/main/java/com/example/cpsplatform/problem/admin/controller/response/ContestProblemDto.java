package com.example.cpsplatform.problem.admin.controller.response;

import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.Section;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ContestProblemDto {

    private Long problemId;
    private String title;
    private int season;
    private Section section;
    private int problemOrder;

    public static ContestProblemDto of(Problem problem){
        return ContestProblemDto.builder()
                .problemId(problem.getId())
                .title(problem.getTitle())
                .season(problem.getContest().getSeason())
                .section(problem.getSection())
                .problemOrder(problem.getProblemOrder())
                .build();
    }

}
