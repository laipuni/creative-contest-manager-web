package com.example.cpsplatform.teamnumber.repository;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.teamnumber.domain.TeamNumber;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamNumberRepository extends JpaRepository<TeamNumber, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select tn from TeamNumber tn where tn.contest.id = :contestId")
    Optional<TeamNumber> getLockedNumberForContest(@Param("contestId") Long contestId);

    Optional<TeamNumber> findByContestId(Long contestId);
}
