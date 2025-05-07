package com.example.cpsplatform.teamsolve.repository;


import com.example.cpsplatform.problem.domain.QProblem;
import com.example.cpsplatform.team.domain.QTeam;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerDto;
import com.example.cpsplatform.teamsolve.domain.QTeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.example.cpsplatform.problem.domain.QProblem.problem;
import static com.example.cpsplatform.team.domain.QTeam.team;
import static com.example.cpsplatform.teamsolve.domain.QTeamSolve.teamSolve;

public class TeamSolveRepositoryCustomImpl implements TeamSolveRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public TeamSolveRepositoryCustomImpl(final EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<GetTeamAnswerDto> findSubmittedAnswersByTeamId(final Long teamId) {
        return queryFactory.select(
                        Projections.constructor(GetTeamAnswerDto.class,
                                teamSolve.id,
                                teamSolve.content,
                                team.id,
                                team.name,
                                problem.section,
                                teamSolve.updatedAt,
                                teamSolve.modifyCount
                        )
                )
                .from(teamSolve)
                .join(teamSolve.problem,problem)
                .join(teamSolve.team,team)
                .where(team.id.eq(teamId)) //해당 팀의 답안지만 조회 하도록 조건 설정
                .orderBy(teamSolve.createdAt.desc()) //생성 기준으로 내림차순으로 정렬
                .fetch();
    }
}
