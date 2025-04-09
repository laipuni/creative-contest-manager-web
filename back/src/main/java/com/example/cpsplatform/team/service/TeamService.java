package com.example.cpsplatform.team.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final ContestRepository contestRepository;

    @Transactional
    public Long createTeam(String leaderId, TeamCreateDto createDto){
        Member leader = memberRepository.findMemberByLoginId(leaderId).orElseThrow();// 없으면 예외

        validateMemberDuplication(leader);
        //중복 참가 확인 필요, 팀원들 아이디 존재유무 확인 필요
        Team team = Team.builder()
                .name(createDto.getTeamName())
                .winner(false)
                .contest(getContestById(createDto.getContestId()))
                .leader(leader)
                .build();

        teamRepository.save(team);
        memberTeamRepository.save(MemberTeam.of(leader, team)); // 팀장도 memberteam에 저장

        if (createDto.getMemberIds().size() > 2) {
            throw new IllegalArgumentException("팀원은 최대 2명까지 등록할 수 있습니다.");
        }

        for (String loginId : createDto.getMemberIds()) {
            Member member = memberRepository.findMemberByLoginId(loginId)
                    .orElseThrow();
            validateMemberDuplication(member);
            memberTeamRepository.save(MemberTeam.of(member, team));
        }

        return team.getId();
    }

    private void validateMemberDuplication(Member member){
        if (memberTeamRepository.existsByMember(member)){
            throw new IllegalArgumentException("이미 다른 팀에 참가한 사용자입니다: " + member.getLoginId());
        }
    }

    private Contest getContestById(Long contestId) {
        return contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회를 찾을 수 없습니다."));
    }
}
