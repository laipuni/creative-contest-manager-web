package com.example.cpsplatform.team.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.DuplicateDataException;
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
    void createTeam() {
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

        // then
        verify(memberTeamRepository, times(1)).save(any(MemberTeam.class));
        verify(teamRepository).save(any(Team.class));
    }

    @DisplayName("팀원이 3명 초과할 경우 예외가 발생한다.")
    @Test
    void createTeamOverMember() {
        // given
        String leaderId = "yi";
        Member leader = Member.builder().loginId(leaderId).build();
        Contest contest = Contest.builder().title("테스트대회").build();
        when(memberRepository.findMemberByLoginId("yi")).thenReturn(Optional.of(leader));
        when(contestRepository.findById(1L)).thenReturn(Optional.of(contest));

        List<String> tooManyMembers = List.of("one", "two", "three");

        TeamCreateDto dto = new TeamCreateDto("2팀", 1L, tooManyMembers);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> teamService.createTeam(leaderId, dto));
    }

    @DisplayName("팀장이 아닌 자가 팀 정보를 수정하려 하면 예외가 발생한다.")
    @Test
    void updateTeam() {
        // given
        String notLeaderId = "가짜리더";
        Member leader = Member.builder().loginId("진짜리더").build();
        Team team = Team.builder().leader(leader).build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> teamService.updateTeam(1L, new TeamUpdateDto("팀명수정", List.of()), notLeaderId));
    }

    @DisplayName("팀장이 아닌 자가 팀을 삭제하면 예외가 발생한다.")
    @Test
    void deleteTeam() {
        // given
        String notLeaderId = "가짜리더";
        Member leader = Member.builder().loginId("진짜리더").build();
        Team team = Team.builder().leader(leader).build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> teamService.deleteTeam(1L, notLeaderId));
    }

    @DisplayName("팀을 삭제할 경우 MemberTeam에 있는 관련 내용도 삭제된다.")
    @Test
    void deleteTeamCheckMemberTeam() {
        // given
        Member leader = Member.builder().loginId("진짜리더").build();
        Team team = Team.builder().leader(leader).build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        // when
        teamService.deleteTeam(1L, "진짜리더");

        // then
        verify(memberTeamRepository).deleteAllByTeam(team);
    }

    @DisplayName("본인이 소속된 팀 리스트를 성공적으로 반환한다.")
    @Test
    void myTeamInfo() {
        // given
        String loginId = "yi";
        Member member = Member.builder().loginId(loginId).build();

        Team team1 = Team.builder().name("one").leader(member).build();
        Team team2 = Team.builder().name("two").leader(member).build();

        MemberTeam memberTeam1 = MemberTeam.of(member, team1);
        MemberTeam memberTeam2 = MemberTeam.of(member, team2);

        when(memberRepository.findMemberByLoginId(loginId)).thenReturn(Optional.of(member));
        when(memberTeamRepository.findAllByMember(loginId)).thenReturn(List.of(memberTeam1, memberTeam2));

        // when
        List<MyTeamInfoDto> result = teamService.getMyTeamInfo(loginId);

        // then
        assertThat(result.get(0).getTeamName()).isEqualTo("one");
        assertThat(result.get(1).getTeamName()).isEqualTo("two");
    }

}