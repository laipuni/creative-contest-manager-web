package com.example.cpsplatform.teamsolve.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.team.domain.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "team_solve",uniqueConstraints =
       @UniqueConstraint(name = "uk_team_solve_teamid_problemid_type",columnNames = {"team_id","problem_id","team_solve_type"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamSolve extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name = "problem_id")
    private Problem problem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "team_solve_type")
    private TeamSolveType teamSolveType;

    @Column(length = 500)
    @Size(max = 500)
    private String content;

    @Builder
    private TeamSolve(final Team team, final Problem problem, final String content, final TeamSolveType teamSolveType) {
        this.team = team;
        this.problem = problem;
        this.content = content;
        this.teamSolveType = teamSolveType;
    }

    public static TeamSolve of(final Team team, final Problem problem,final String content,final TeamSolveType teamSolveType){
        return TeamSolve.builder()
                .team(team)
                .problem(problem)
                .content(content)
                .teamSolveType(teamSolveType)
                .build();
    }

    public void submit(){
        this.teamSolveType =  TeamSolveType.SUBMITTED;
    }

    public void modifyContent(String content){
        this.content = content;
    }
}
