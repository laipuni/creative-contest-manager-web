package com.example.cpsplatform.member.repository;

import com.example.cpsplatform.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findMemberByLoginId(String loginId);

}
