package com.example.cpsplatform.contest.repository;

import com.example.cpsplatform.contest.Contest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository extends JpaRepository<Contest,Long> {
}
