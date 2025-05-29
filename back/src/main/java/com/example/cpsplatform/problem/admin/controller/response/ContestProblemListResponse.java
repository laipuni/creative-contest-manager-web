package com.example.cpsplatform.problem.admin.controller.response;

import com.example.cpsplatform.utils.PagingUtils;
import com.example.cpsplatform.problem.domain.Problem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.example.cpsplatform.problem.admin.service.ContestProblemAdminService.PROBLEM_SIZE;

@Getter
@Builder
@AllArgsConstructor
public class ContestProblemListResponse {

    private int totalPage;
    private int page;
    private int firstPage;
    private int lastPage;
    private int size;
    private List<ContestProblemDto> problemList;

    public static ContestProblemListResponse of(final Page<Problem> result) {
        int firstPage = PagingUtils.getStartPage(result.getNumber(), PROBLEM_SIZE);
        int lastPage = PagingUtils.getEndPage(firstPage,result.getTotalPages());

        return ContestProblemListResponse.builder()
                .totalPage(result.getTotalPages())
                .page(result.getNumber())
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size((int)result.getTotalElements())
                .problemList(
                        result.getContent().stream()
                        .map(ContestProblemDto::of)
                        .toList()
                )
                .build();
    }
}
