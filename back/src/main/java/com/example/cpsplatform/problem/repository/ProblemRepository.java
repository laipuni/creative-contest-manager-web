package com.example.cpsplatform.problem.repository;

import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem,Long> {

    //관리자 전용, 출제용 문제 조회 기능입니다.
    @EntityGraph(value = "contest")
    @Query("SELECT p FROM Problem p WHERE p.contest.id = :contestId AND p.problemType = :problemType ORDER BY p.section ASC, p.problemOrder ASC")
    Page<Problem> findContestProblemsByContestAndProblemType(Pageable pageable, @Param("problemType") ProblemType problemType, @Param("contestId") Long contestId);

    @EntityGraph(value = "contest")
    @Query("SELECT p FROM Problem p WHERE p.contest.id = :contestId AND p.id = :problemId AND p.problemType = :problemType")
    Optional<Problem> findContestProblemByContestIdAndProblemId(@Param("problemType") ProblemType problemType, @Param("problemId") Long problemId, @Param("contestId") Long contestId);

    @Query("""
        SELECT p FROM Problem p
        LEFT JOIN FETCH p.files
        WHERE p.contest.id = :contestId
          AND p.section = :section
    """)
    Optional<Problem> findWithFilesByContestIdAndSection(@Param("contestId") Long contestId, @Param("section") Section section);

    List<Problem> findAllByContestId(Long contestId);

    @Modifying
    @Query("delete from Problem where id in :problemIds")
    void hardDeleteAllByIdIn(@Param("problemIds") List<Long> problemIds);
}
