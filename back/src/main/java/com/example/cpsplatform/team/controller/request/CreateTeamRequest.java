package com.example.cpsplatform.team.controller.request;

import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateTeamRequest {
    private String teamName;
    private Long contestId;
    private List<String> memberIds;

    public TeamCreateDto toServiceDto() {
        return new TeamCreateDto(teamName, contestId, memberIds);
    }
}
