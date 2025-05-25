package com.example.cpsplatform.member.repository;

import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    @EntityGraph(attributePaths = "organization")
    Optional<Member> findMemberByLoginId(String loginId);

    //관리자가 아닌 아이디와 일치하는 유저찾는 쿼리
    @EntityGraph(attributePaths = "organization")
    Optional<Member> findMemberByLoginIdAndRole(String loginId,Role role);

    //관리자가 아닌 이메일과 일치하는 유저 찾는 쿼리
    @EntityGraph(attributePaths = "organization")
    Optional<Member> findMemberByEmailAndRole(String email,Role role);

    //관리자가 아닌 이메일과 아이디가 일치하는 유저 찾는 쿼리
    @EntityGraph(attributePaths = "organization")
    Optional<Member> findMemberByEmailAndLoginIdAndRole(String email,String loginId, Role role);

    boolean existsByLoginId(String LoginId);

    @Query("select mt.member from MemberTeam mt where mt.team.id = :teamId")
    List<Member> findAllByTeamId(@Param("teamId") Long teamId);
}
