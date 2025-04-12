package com.example.cpsplatform.team.repository;

import com.example.cpsplatform.team.domain.Team;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @EntityGraph(attributePaths = {"leader"})
    @Query("select t from MemberTeam mt " +
            "join mt.team t " + // 명시적 조인으로 on mt.team.id = t.id
            "join mt.member m " + // mt.member.id = m.id 자동으로 쿼리 날릴때 넣어줌
            "where m.loginId = :loginId")
    List<Team> findTeamByMemberLoginId(@Param("loginId") String loginId);
}
