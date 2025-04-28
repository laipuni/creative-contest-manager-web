package com.example.cpsplatform.team.service.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyTeamInfoDto {
    private Long teamId;
    private String teamName;
    private String loginId;
    private LocalDateTime createdAt;
}
