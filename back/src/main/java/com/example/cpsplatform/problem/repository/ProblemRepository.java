package com.example.cpsplatform.problem.repository;

import com.example.cpsplatform.problem.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
}
