package com.example.cpsplatform.contest.admin.controller.response;

import com.example.cpsplatform.contest.Contest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class DeletedContestListResponse {

    private List<DeletedContestDto> deletedContestList;

    public static DeletedContestListResponse of(List<Contest> contestList){
        return DeletedContestListResponse.builder()
                .deletedContestList(contestList.stream()
                        .map(DeletedContestDto::of)
                        .toList()
                )
                .build();
    }


}
