package com.example.cpsplatform.member.repository;

import com.example.cpsplatform.member.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    @EntityGraph(attributePaths = "organization")
    Optional<Member> findMemberByLoginId(String loginId);
    @EntityGraph(attributePaths = "organization")
    Optional<Member> findMemberByEmail(String email);

    @EntityGraph(attributePaths = "organization")
    Optional<Member> findMemberByEmailAndLoginId(String email,String loginId);

}
