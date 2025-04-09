package com.example.cpsplatform.team.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TeamServiceTest {
    private MemberRepository memberRepository = mock(MemberRepository.class);
    private TeamRepository teamRepository = mock(TeamRepository.class);
    private MemberTeamRepository memberTeamRepository = mock(MemberTeamRepository.class);
    private ContestRepository contestRepository = mock(ContestRepository.class);

    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(memberRepository, teamRepository, memberTeamRepository, contestRepository);
    }

    @DisplayName("팀장과 멤버가 주어질 경우 팀 생성이 정상적으로 동작한다.")
    @Test
    void create_team_success() {
        // given
        String leaderId = "yi";
        Member leader = Member.builder().loginId(leaderId).build();
        Member member1 = Member.builder().loginId("one").build();
        Member member2 = Member.builder().loginId("two").build();
        Contest contest = Contest.builder().title("테스트대회").build();
        Long contestId = 1L;

        TeamCreateDto dto = new TeamCreateDto("팀입니다", contestId, List.of("one", "two"));

        when(memberRepository.findMemberByLoginId("yi")).thenReturn(Optional.of(leader));
        when(memberRepository.findMemberByLoginId("one")).thenReturn(Optional.of(member1));
        when(memberRepository.findMemberByLoginId("two")).thenReturn(Optional.of(member2));
        when(contestRepository.findById(contestId)).thenReturn(Optional.of(contest));
        when(memberTeamRepository.existsByMember(any())).thenReturn(false);

        Team savedTeam = Team.builder().leader(leader).contest(contest).name("팀입니다").build();
        when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);

        // when
        Long resultTeamId = teamService.createTeam(leaderId, dto);

        // then
        verify(memberTeamRepository, times(3)).save(any(MemberTeam.class));
        verify(teamRepository).save(any(Team.class));
    }

}