package com.example.cpsplatform.contest.admin.request;

import com.example.cpsplatform.contest.admin.service.dto.WinnerTeamsDto;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WinnerTeamsRequest {

    @NotEmpty(message = "본선에 진출할 팀들의 정보는 필수입니다.")
    private List<Long> teamIds;

    public WinnerTeamsDto toWinnerTeamsDto(){
        return new WinnerTeamsDto(teamIds);
    }
}
