package com.example.cpsplatform.teamsolve.repository;


import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListDto;
import com.example.cpsplatform.teamsolve.admin.controller.response.TeamSolveListResponse;
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

    /**
     * 유저용 팀에서 작성한 답안지를 유형에 맞게 조회
     * 만약 설정한 유형이 없다면 모든 유형의 답안지를 조회
     * @param teamId 팀의 pk
     * @param teamSolveType 답안지
     * @return
     */
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

    /**
     * 관리자 용 팀에서 작성한 답안지를 유형에 맞게 조회
     * 만약 설정한 유형이 없다면 모든 유형의 답안지를 조회
     * 분리한 이유는 추후에 검색 필터, 정렬기준과같이 요구사항에 확장성을 고려해서 분리
     * @param teamId 팀의 pk
     * @param teamSolveType 답안지
     * @return
     */
    @Override
    public TeamSolveListResponse findTeamSolveByAdminCond(final Long teamId, final TeamSolveType teamSolveType) {
        List<TeamSolveListDto> result = queryFactory.select(
                        Projections.constructor(TeamSolveListDto.class,
                                problem.id,
                                problem.title,
                                problem.problemOrder,
                                problem.section,
                                teamSolve.id,
                                teamSolve.teamSolveType,
                                teamSolve.updatedAt
                        )
                )
                .from(teamSolve)
                .join(teamSolve.problem, problem)
                .join(teamSolve.team, team)
                .where(team.id.eq(teamId).and(filterAdminByType(teamSolveType))) //해당 팀의 답안지, 임시, 최종 제출 확인
                .orderBy(teamSolve.createdAt.desc()) //생성 기준으로 내림차순으로 정렬
                .fetch();
        return new TeamSolveListResponse(result);
    }

    private static BooleanBuilder filterAdminByType(final TeamSolveType teamSolveType) {
        if(teamSolveType == null){
            //만약 제출 타입 지정하지 않았을 경우 모든 타입 반환
            return new BooleanBuilder();
        }
        return new BooleanBuilder(teamSolve.teamSolveType.eq(teamSolveType));
    }
}
