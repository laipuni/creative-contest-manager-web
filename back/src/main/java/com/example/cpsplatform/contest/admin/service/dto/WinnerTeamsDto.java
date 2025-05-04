package com.example.cpsplatform.contest.admin.service.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WinnerTeamsDto {

    private List<Long> teamIds;
}
