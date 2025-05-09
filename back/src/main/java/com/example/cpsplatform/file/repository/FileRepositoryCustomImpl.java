package com.example.cpsplatform.file.repository;

import com.example.cpsplatform.contest.QContest;
import com.example.cpsplatform.file.domain.QFile;
import com.example.cpsplatform.file.repository.dto.FileNameDto;
import com.example.cpsplatform.problem.domain.QProblem;
import com.example.cpsplatform.team.domain.QTeam;
import com.example.cpsplatform.teamsolve.domain.QTeamSolve;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.hibernate.sql.Alias;

import java.util.List;

import static com.example.cpsplatform.contest.QContest.contest;
import static com.example.cpsplatform.file.domain.QFile.file;
import static com.example.cpsplatform.problem.domain.QProblem.problem;
import static com.example.cpsplatform.team.domain.QTeam.team;
import static com.example.cpsplatform.teamsolve.domain.QTeamSolve.teamSolve;

public class FileRepositoryCustomImpl implements FileRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public FileRepositoryCustomImpl(final EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<FileNameDto> findFileNameDto(final List<Long> fileIds) {
        return queryFactory.select(Projections.constructor(FileNameDto.class,
                        file.id,
                        file.extension,
                        problem.section,
                        team.name,
                        contest.season,
                        problem.problemOrder
                ))
                .from(file)
                .join(file.teamSolve, teamSolve)
                .join(teamSolve.problem, problem)
                .join(teamSolve.team, team)
                .join(team.contest, contest)
                .where(file.id.in(fileIds), file.deleted.isFalse())
                .fetch();
    }

    @Override
    public List<Long> findFileIdsByContestIdInTeamSolve(final Long contestId) {
        return queryFactory.select(file.id)
                .from(file)
                .join(file.teamSolve, teamSolve)
                .join(teamSolve.team, team)
                .join(team.contest, contest)
                .where(contest.id.eq(contestId))
                .fetch();
    }
}
