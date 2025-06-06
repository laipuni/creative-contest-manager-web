package com.example.cpsplatform.team.repository;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.team.domain.Team;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @EntityGraph(attributePaths = {"leader"})
    @Query("select t from MemberTeam mt " +
            "join mt.team t " + // 명시적 조인으로 on mt.team.id = t.id
            "join mt.member m " + // mt.member.id = m.id 자동으로 쿼리 날릴때 넣어줌
            "where m.loginId = :loginId")
    List<Team> findTeamByMemberLoginId(@Param("loginId") String loginId);

    @Query("SELECT t FROM MemberTeam mt " +
            "JOIN mt.team t " +
            "WHERE mt.member.loginId = :loginId AND t.contest.id = :contestId")
    Optional<Team> findTeamByMemberAndContest(@Param("loginId") String loginId, @Param("contestId") Long contestId);

    @EntityGraph(attributePaths = {"leader"})
    Page<Team> findTeamListByContest(Contest contest, Pageable pageable);


    //해당 대회에 팀장으로 맡고있는 팀이 존재하는지 여부를 확인하는 쿼리
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Team t where t.contest.id = :contestId and t.leader.loginId = :leaderLoginId")
    Optional<Team> findTeamByContestIdAndLeaderIdWithLock(@Param("contestId") Long contestId, @Param("leaderLoginId") String leaderLoginId);


    //유저의 아이디와 대회의 id를 받아서 해당 대회에 참여한 팀의 정보를 조회하는 쿼리
    @EntityGraph(attributePaths = {"leader"})
    @Query("select t from MemberTeam mt " +
            "join mt.team t " +
            "join mt.member m " +
            "where m.loginId = :loginId and t.contest.id = :contestId")
    Optional<Team> findTeamByMemberLoginIdAndContestId(@Param("loginId") String loginId,@Param("contestId") Long contestId);

    //네이티브 쿼리라서 임시 삭제된 대회의 팀까지 불러올 수 있으니 주의
    @Query(value = "select * from team where contest_id = :contestId"
            ,nativeQuery = true)
    List<Team> findAllByContestIdNative(@Param("contestId") Long contestId);


}
