package com.example.cpsplatform.team.repository;

import com.example.cpsplatform.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
