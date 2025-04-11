package com.example.cpsplatform.memberteam.repository;

import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.team.domain.Team;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberTeamRepository extends JpaRepository<MemberTeam, Long> {
    boolean existsByMember(Member member);
    void deleteAllByTeam(Team team);

    @Modifying
    @Query("DELETE FROM MemberTeam mt WHERE mt.team = :team AND mt.member != :leader")
    void deleteAllByTeamExceptLeader(@Param("team") Team team, @Param("leader") Member leader);

    @EntityGraph(attributePaths = {"team", "team.leader"})
    List<MemberTeam> findAllByMember(String memberId);
}
