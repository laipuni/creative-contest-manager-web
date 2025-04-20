package com.example.cpsplatform.team.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.team.service.dto.MyTeamInfoByContestDto;
import com.example.cpsplatform.team.service.dto.MyTeamInfoDto;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import com.example.cpsplatform.team.service.dto.TeamUpdateDto;
import com.example.cpsplatform.teamnumber.domain.TeamNumber;
import com.example.cpsplatform.teamnumber.repository.TeamNumberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final TeamNumberRepository teamNumberRepository;

    @Transactional
    public Long createTeam(String leaderId, TeamCreateDto createDto){
        Member leader = memberRepository.findMemberByLoginId(leaderId)
                .orElseThrow(()->new IllegalArgumentException("해당 팀장은 존재하지 않습니다."));

        Contest contest = contestRepository.findById(createDto.getContestId())
                .orElseThrow(()->new IllegalArgumentException("해당 대회는 존재하지 않습니다."));

        TeamNumber teamNumber = teamNumberRepository.getLockedNumberForContest(createDto.getContestId())
                .orElseGet(()->teamNumberRepository.save(TeamNumber.of(contest, 0)));

        String teamIdNumber = teamNumber.getNextTeamNumber();
        Team team = buildTeam(createDto, leader, teamIdNumber);

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
        team.isNotTeamLeader(team, loginId);

        team.updateTeamName(updateDto.getTeamName());
        memberTeamRepository.deleteAllByTeamExceptLeader(team, team.getLeader());
        addMembersToTeam(updateDto.getMemberIds(), team);
    }

    @Transactional
    public void deleteTeam(Long teamId, String loginId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()->new IllegalArgumentException("해당 팀은 존재하지 않습니다."));
        team.isNotTeamLeader(team, loginId);

        memberTeamRepository.deleteAllByTeam(team);
        teamRepository.delete(team);
    }

    public List<MyTeamInfoDto> getMyTeamInfo(String loginId){
        Member member = memberRepository.findMemberByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀원은 존재하지 않습니다."));

        return teamRepository.findTeamByMemberLoginId(member.getLoginId())
                .stream()
                .map(team -> new MyTeamInfoDto(
                        team.getId(), team.getName(), team.getLeader().getLoginId(), team.getCreatedAt()
                )).toList();
    }

    public MyTeamInfoByContestDto getMyTeamInfoByContest(Long contestId, String loginId){
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(()->new IllegalArgumentException("해당 대회는 존재하지 않습니다."));
        Member member = memberRepository.findMemberByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀원은 존재하지 않습니다."));
        Team team = teamRepository.findTeamByMemberAndContest(member.getLoginId(),contest.getId())
                .orElseThrow(()->new IllegalArgumentException("해당하는 팀이 존재하지 않습니다."));

        List<MemberTeam> memberTeams = memberTeamRepository.findAllByTeamId(team.getId());
        List<String> memberIds = memberTeams.stream()
                .map(mt -> mt.getMember().getLoginId())
                .toList(); //본인이 속한 팀의 멤버들 리스트

        return new MyTeamInfoByContestDto(
                team.getId(),
                team.getName(),
                team.getLeader(),
                memberIds,
                team.getCreatedAt());
    }

    private void addMembersToTeam(List<String> memberIds, Team team) {
        List<MemberTeam> memberTeams = new ArrayList<>();

        for (String loginId : memberIds) {
            Member member = memberRepository.findMemberByLoginId(loginId)
                    .orElseThrow(()->new IllegalArgumentException("해당 팀원은 존재하지 않습니다."));
            memberTeams.add(MemberTeam.of(member, team));
        }
        memberTeamRepository.saveAll(memberTeams);
    }

    private Team buildTeam(TeamCreateDto createDto, Member leader, String teamIdNumber) {
        return Team.of(
                createDto.getTeamName(),
                false,
                leader,
                getContestById(createDto.getContestId()),
                teamIdNumber);
    }

    private void validateTeamSize(List<String> memberIds) {
        if (memberIds.isEmpty() || memberIds.size() > 2) {
            throw new IllegalArgumentException("팀원은 최대 2명까지 등록할 수 있습니다.");
        }
    }

    private Contest getContestById(Long contestId) {
        return contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회를 찾을 수 없습니다."));
    }
}
