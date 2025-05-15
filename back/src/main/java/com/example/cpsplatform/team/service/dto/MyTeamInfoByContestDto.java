package com.example.cpsplatform.team.service.dto;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.team.domain.Team;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyTeamInfoByContestDto {
    private Long teamId;
    private String teamName;
    private String leaderLoginId;
    private String leaderName;
    private List<MyTeamMemberDto> members;
    private LocalDateTime createdAt;
    private Long contestId;

    public static MyTeamInfoByContestDto of(final Team team, final List<Member> members, final Contest contest){
        return MyTeamInfoByContestDto.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .leaderLoginId(team.getLeader().getLoginId())
                .leaderName(team.getLeader().getName())
                .members(
                        members.stream()
                                .map(MyTeamMemberDto::of)
                                .toList()
                )
                .createdAt(team.getCreatedAt())
                .contestId(contest.getId())
                .build();
    }

}
