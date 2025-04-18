package com.example.cpsplatform.teamsolve.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.team.domain.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
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
@Table(name = "Team_solve")
public class TeamSolve extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "modify_count")
    private Integer modifyCount;

    @Builder
    private TeamSolve(final Team team, final Problem problem, final Integer modifyCount){
        this.team = team;
        this.problem = problem;
        this.modifyCount = modifyCount;
    }
}
