package com.example.cpsplatform.contest.admin.request;

import com.example.cpsplatform.contest.admin.service.dto.WinnerTeamsDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WinnerTeamsRequest {

    private List<Long> teamIds;

    public WinnerTeamsDto toWinnerTeamsDto(){
        return new WinnerTeamsDto(teamIds);
    }
}
