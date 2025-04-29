package com.example.cpsplatform.problem.admin.service.dto;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AddProblemDto {

    private Long contestId;
    private String title;
    private Section section;
    private String content;
    private Integer problemOrder;

    public Problem createContestProblem(Contest contest){
        return Problem.createContestProblem(title,contest,section,content,problemOrder);
    }

}
