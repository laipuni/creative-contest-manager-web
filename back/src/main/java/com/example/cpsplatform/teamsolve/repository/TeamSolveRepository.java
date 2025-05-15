package com.example.cpsplatform.teamsolve.repository;

import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamSolveRepository extends JpaRepository<TeamSolve,Long>, TeamSolveRepositoryCustom{
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ts from TeamSolve ts where ts.team.id = :teamId and ts.problem.id = :problemId")
    Optional<TeamSolve> findByTeamIdAndProblemId(@Param("teamId") Long teamId, @Param("problemId") Long problemId);

    //팀 id 리스트로 팀들의 답안지를 불러오는 쿼리
    List<TeamSolve> findAllByTeam_IdIn(List<Long> teamIds);

}
