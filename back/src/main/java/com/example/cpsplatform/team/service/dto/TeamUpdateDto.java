package com.example.cpsplatform.team.service.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamUpdateDto {
    private String teamName;
    private List<String> memberIds;
    private Long contestId;
}
