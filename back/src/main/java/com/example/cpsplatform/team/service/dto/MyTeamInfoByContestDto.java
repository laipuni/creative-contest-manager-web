package com.example.cpsplatform.team.service.dto;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.team.domain.Team;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyTeamInfoByContestDto {
    private Long teamId;
    private String teamName;
    private String leaderLoginId;
    private List<String> memberIds;
    private LocalDateTime createdAt;

}
