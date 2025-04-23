package com.example.cpsplatform.teamsolve.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_solve")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamSolve extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Problem problem;

    @Column(name = "modify_count")
    private int modifyCount;

    @Builder
    private TeamSolve(final Team team, final Problem problem) {
        this.team = team;
        this.problem = problem;
        this.modifyCount = 0;
    }

    public static TeamSolve of(final Team team, final Problem problem){
        return TeamSolve.builder()
                .team(team)
                .problem(problem)
                .build();
    }

    public void incrementModifyCount(){
        modifyCount +=1;
    }
}
