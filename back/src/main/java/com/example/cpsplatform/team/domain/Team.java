package com.example.cpsplatform.team.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"contest_id", "team_number"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Boolean winner = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private Member leader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @Column(name = "team_number", nullable = false)
    private String teamNumber;

    @Builder
    private Team(final String name, final Boolean winner, final Member leader, final Contest contest, final String teamNumber){
        this.name = name;
        this.winner = winner;
        this.leader = leader;
        this.contest = contest;
        this.teamNumber = teamNumber;
    }

    public static Team of(final String name, final Boolean winner, final Member leader, final Contest contest, final String teamNumber){
        return Team.builder()
                .name(name)
                .winner(winner)
                .leader(leader)
                .contest(contest)
                .teamNumber(teamNumber)
                .build();
    }

    public void updateTeamName(String newName) {
        if (newName != null && !newName.isBlank()) {
            this.name = newName;
        }
    }

    public void isNotTeamLeader(Team team, String leaderId) {
        if (!team.getLeader().getLoginId().equals(leaderId)) {
            throw new IllegalArgumentException("팀장만 수정 또는 삭제할 수 있습니다.");
        }
    }
}
