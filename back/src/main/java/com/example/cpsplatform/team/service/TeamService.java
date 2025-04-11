package com.example.cpsplatform.team.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.team.service.dto.MyTeamInfoDto;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import com.example.cpsplatform.team.service.dto.TeamUpdateDto;
import java.util.List;
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
        Member leader = memberRepository.findMemberByLoginId(leaderId)
                .orElseThrow(()->new IllegalArgumentException("해당 팀장은 존재하지 않습니다."));
        validateMemberDuplication(leader);

        Team team = buildTeam(createDto, leader);
        teamRepository.save(team);
        memberTeamRepository.save(MemberTeam.of(leader, team));

        validateTeamSize(createDto.getMemberIds());
        addMembersToTeam(createDto.getMemberIds(), team);
        return team.getId();
    }

    @Transactional
    public void updateTeam(Long teamId, TeamUpdateDto updateDto, String loginId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()->new IllegalArgumentException("해당 팀은 존재하지 않습니다."));
        validateTeamLeader(team, loginId);

        team.updateTeamName(updateDto.getTeamName());
        memberTeamRepository.deleteAllByTeamExceptLeader(team, team.getLeader());
        addMembersToTeam(updateDto.getMemberIds(), team);
    }

    @Transactional
    public void deleteTeam(Long teamId, String loginId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()->new IllegalArgumentException("해당 팀은 존재하지 않습니다."));
        validateTeamLeader(team, loginId);

        memberTeamRepository.deleteAllByTeam(team);
        teamRepository.delete(team);
    }

    public List<MyTeamInfoDto> getMyTeamInfo(String loginId){
        Member member = memberRepository.findMemberByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀원은 존재하지 않습니다."));

        return memberTeamRepository.findAllByMember(member)
                .stream().map(MemberTeam::getTeam)
                .map(team -> new MyTeamInfoDto(
                        team.getId(), team.getName(), team.getLeader().getLoginId(), team.getCreatedAt()
                )).toList();
    }

    private void validateTeamLeader(Team team, String leaderId) {
        if (!team.getLeader().getLoginId().equals(leaderId)) {
            throw new IllegalArgumentException("팀장만 수정 또는 삭제할 수 있습니다.");
        }
    }

    private void addMembersToTeam(List<String> memberIds, Team team) {
        for (String loginId : memberIds) {
            Member member = memberRepository.findMemberByLoginId(loginId)
                    .orElseThrow(()->new IllegalArgumentException("해당 팀원은 존재하지 않습니다."));
            validateMemberDuplication(member);
            memberTeamRepository.save(MemberTeam.of(member, team));
        }
    }

    private Team buildTeam(TeamCreateDto createDto, Member leader) {
        return Team.builder()
                .name(createDto.getTeamName())
                .winner(false)
                .contest(getContestById(createDto.getContestId()))
                .leader(leader)
                .build();
    }

    private void validateTeamSize(List<String> memberIds) {
        if (memberIds.isEmpty() || memberIds.size() > 2) {
            throw new IllegalArgumentException("팀원은 최대 2명까지 등록할 수 있습니다.");
        }
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
