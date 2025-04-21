package com.example.cpsplatform.contest.admin.controller.response;

import static com.example.cpsplatform.contest.admin.service.ContestAdminService.CONTEST_PAGE_SIZE;

import com.example.cpsplatform.PagingUtils;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.team.domain.Team;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
@AllArgsConstructor
public class TeamListByContestResponse {
    private int totalPage;
    private int page;
    private int firstPage;
    private int lastPage;
    private int size;
    private List<TeamListByContestDto> teamList;

    public static TeamListByContestResponse of(final Page<Team> teamPage){
        int firstPage = PagingUtils.getStartPage(teamPage.getNumber(), CONTEST_PAGE_SIZE);
        int lastPage = PagingUtils.getEndPage(firstPage,teamPage.getTotalPages());

        return TeamListByContestResponse.builder()
                .totalPage(teamPage.getTotalPages())
                .page(teamPage.getNumber())
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size((int)teamPage.getTotalElements())
                .teamList(
                        teamPage.getContent().stream()
                                .map(TeamListByContestDto::of)
                                .toList()
                )
                .build();
    }
}
