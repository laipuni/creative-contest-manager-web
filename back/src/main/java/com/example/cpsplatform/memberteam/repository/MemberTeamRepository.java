package com.example.cpsplatform.memberteam.repository;

import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTeamRepository extends JpaRepository<MemberTeam, Long> {
    boolean existsByMember(Member member);
}
