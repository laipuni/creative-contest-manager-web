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

    @EntityGraph(attributePaths = {"member","team"})
    List<MemberTeam> findAllByTeamId(Long teamId);

    @Modifying
    @Query("DELETE FROM MemberTeam mt WHERE mt.team = :team AND mt.member != :leader")
    void deleteAllByTeamExceptLeader(@Param("team") Team team, @Param("leader") Member leader);

    //해당 유저가 해당 대회에 참여한 팀이 있는지 확인하는 쿼리
    @Query(value = "select exists " +
            "(select mt " +
            "from MemberTeam mt " +
            "where mt.member.loginId = :loginId " +
            "and mt.team.contest.id = :contestId)")
    boolean existsByContestIdAndLoginId(@Param("contestId") Long contestId, @Param("loginId") String loginId);

    //해당 대회에 참여자를 조회하는 쿼리
    @Query("select mt from MemberTeam mt join fetch mt.member m join fetch mt.team t where t.contest.id = :contestId")
    List<MemberTeam> findAllByContestId(@Param("contestId") Long contestId);

    //유저가 해당 팀에 속해있는지 확인하는 쿼리
    @Query(value = "select exists (select 1 from MemberTeam mt where mt.team.id = :teamId and  mt.member.loginId = :loginId)")
    boolean existsByTeamIdAndLoginId(@Param("teamId") Long teamId, @Param("loginId") String loginId);

    @Modifying
    @Query(value = "delete from MemberTeam where id in :memberTeamIds",nativeQuery = true)
    void hardDeleteAllByIdIn(@Param("memberTeamIds") List<Long> memberTeamIds);
}
