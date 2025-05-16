package com.example.cpsplatform.memberteam.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_team", uniqueConstraints =
        @UniqueConstraint(name = "uk_member_team_memberid_teamid", columnNames = {"member_id","team_id"})
)
public class MemberTeam extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Builder
    private MemberTeam(Member member, Team team) {
        this.member = member;
        this.team = team;
    }

    public static MemberTeam of(Member member, Team team) {
        return MemberTeam.builder()
                .member(member)
                .team(team)
                .build();
    }
}
