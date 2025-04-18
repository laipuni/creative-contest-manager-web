package com.example.cpsplatform.problem.admin.controller.request;

import com.example.cpsplatform.problem.admin.service.dto.UpdateProblemDto;
import com.example.cpsplatform.problem.domain.Section;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateContestProblemRequest {

    @NotBlank(message = "등록할 문제의 제목은 필수입니다.")
    private String title;

    @NotNull(message = "등록할 문제의 섹션은 필수입니다.")
    private Section section;

    private String content;

    @Min(value = 1, message = "출제할 문제의 번호는 0보다 큰 수여야 합니다.")
    private Integer problemOrder;

    private List<Long> deleteFileIds;

    public UpdateProblemDto toUpdateProblemDto(final Long contestId, final Long problemId){
        return new UpdateProblemDto(
                problemId,
                contestId,
                title,
                section,
                content,
                problemOrder,
                deleteFileIds.isEmpty() ? Collections.emptyList() : deleteFileIds
        );
    }

}
