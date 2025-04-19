package com.example.cpsplatform.contest.admin.controller.response;

import com.example.cpsplatform.PagingUtils;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.example.cpsplatform.contest.admin.service.ContestAdminService.CONTEST_PAGE_SIZE;


@Getter
@Builder
@AllArgsConstructor
public class ContestListResponse {

    private int totalPage;
    private int page;
    private int firstPage;
    private int lastPage;
    private int size;
    private List<ContestListDto> problemList;

    public static ContestListResponse of(final Page<Contest> contestPage){
        int firstPage = PagingUtils.getStartPage(contestPage.getNumber(), CONTEST_PAGE_SIZE);
        int lastPage = PagingUtils.getEndPage(firstPage,contestPage.getTotalPages());

        return ContestListResponse.builder()
                .totalPage(contestPage.getTotalPages())
                .page(contestPage.getNumber())
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size((int)contestPage.getTotalElements())
                .problemList(
                        contestPage.getContent().stream()
                                .map(ContestListDto::of)
                                .toList()
                )
                .build();
    }

}
