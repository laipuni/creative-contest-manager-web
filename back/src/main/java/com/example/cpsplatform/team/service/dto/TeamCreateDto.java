package com.example.cpsplatform.team.service.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamCreateDto {
    private String teamName;
    private Long contestId;
    private List<String> memberIds;


}
