package com.example.cpsplatform.contest.repository;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContestRepository extends JpaRepository<Contest,Long> {

    //관리자 전용, 대회 리스트 조회
    @Query("SELECT c FROM Contest c ORDER BY c.season ASC")
    Page<Contest> findContestList(Pageable pageable);

}

