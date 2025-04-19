package com.example.cpsplatform.problem.admin.service.dto;

import com.example.cpsplatform.problem.domain.Section;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UpdateProblemDto {
    private Long problemId;
    private Long contestId;
    private String title;
    private Section section;
    private String content;
    private Integer problemOrder;
    private List<Long> deleteFileIds;

    public boolean hasDeleteFile(){
        return !deleteFileIds.isEmpty();
    }

}
