package com.example.cpsplatform.teamsolve.repository;

import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamSolveRepository extends JpaRepository<TeamSolve,Long>, TeamSolveRepositoryCustom{
    //해당 문제에 제출한 팀의 답안지 정보를 조회하는 쿼리
    @Query("select ts from TeamSolve ts where ts.team.id = :teamId and ts.problem.id = :problemId and ts.teamSolveType = :teamSolveType")
    Optional<TeamSolve> findByTeamIdAndProblemId(@Param("teamId") Long teamId, @Param("problemId") Long problemId, @Param("teamSolveType")TeamSolveType teamSolveType);

    //해당 대회의 답안지 정보들을 조회하는 쿼리
    @Query("select ts from TeamSolve ts join ts.team t where t.id = :teamId and t.contest.id = :contestId and ts.teamSolveType = :teamSolveType")
    List<TeamSolve> findAllByTeamIdAndContestIdAndTeamSolveType(@Param("teamId") Long teamId, @Param("contestId") Long contestId, @Param("teamSolveType")TeamSolveType teamSolveType);


    //팀 id 리스트로 팀들의 답안지를 불러오는 쿼리
    @Query(value = "select * from team_solve where team_id IN (:teamIds)"
            ,nativeQuery = true)
    List<TeamSolve> findAllByTeam_IdInNative(@Param("teamIds") List<Long> teamIds);

}
