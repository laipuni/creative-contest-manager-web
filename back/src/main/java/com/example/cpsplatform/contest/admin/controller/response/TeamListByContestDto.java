package com.example.cpsplatform.contest.admin.controller.response;

import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.team.domain.Team;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TeamListByContestDto {

    private Long teamId;
    private String name;
    private Boolean winner;
    private Member leader;
    private String teamNumber;
    private LocalDateTime createdAt;

    public static TeamListByContestDto of(Team team){
        return TeamListByContestDto.builder()
                .teamId(team.getId())
                .name(team.getName())
                .winner(team.getWinner())
                .leader(team.getLeader())
                .teamNumber(team.getTeamNumber())
                .createdAt(team.getCreatedAt())
                .build();
    }
}
