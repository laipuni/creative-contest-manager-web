package com.example.cpsplatform.memberteam.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.team.domain.Team;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Member_team")
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
