package com.example.cpsplatform.teamsolve.repository;


import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerDto;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.querydsl.core.BooleanBuilder;
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
    public List<GetTeamAnswerDto> findSubmittedAnswersByTeamId(final Long teamId, final TeamSolveType teamSolveType) {
        return queryFactory.select(
                        Projections.constructor(GetTeamAnswerDto.class,
                                teamSolve.id,
                                teamSolve.content,
                                team.id,
                                team.name,
                                problem.section,
                                teamSolve.teamSolveType,
                                teamSolve.updatedAt
                        )
                )
                .from(teamSolve)
                .join(teamSolve.problem,problem)
                .join(teamSolve.team,team)
                .where(team.id.eq(teamId).and(filterByType(teamSolveType))) //해당 팀의 답안지, 임시, 최종 제출 확인
                .orderBy(teamSolve.createdAt.desc()) //생성 기준으로 내림차순으로 정렬
                .fetch();
    }

    private static BooleanBuilder filterByType(final TeamSolveType teamSolveType) {
        if(teamSolveType == null){
            //만약 제출 타입 지정하지 않았을 경우 모든 타입 반환
            return new BooleanBuilder();
        }
        return new BooleanBuilder(teamSolve.teamSolveType.eq(teamSolveType));
    }
}
