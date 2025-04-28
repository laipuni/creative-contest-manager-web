package com.example.cpsplatform.team.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.security.encoder.CryptoService;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.team.service.dto.MyTeamInfoByContestDto;
import com.example.cpsplatform.team.service.dto.MyTeamInfoDto;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import com.example.cpsplatform.team.service.dto.TeamUpdateDto;
import com.example.cpsplatform.teamnumber.domain.TeamNumber;
import com.example.cpsplatform.teamnumber.repository.TeamNumberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class TeamServiceTest {
    @Autowired
    private TeamService teamService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamNumberRepository teamNumberRepository;

    @Autowired
    private MemberTeamRepository memberTeamRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CryptoService cryptoService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(memberRepository, teamRepository, memberTeamRepository, contestRepository, teamNumberRepository);
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

        TeamNumber teamNumber = TeamNumber.builder().contest(contest).lastTeamNumber(2).build();

        when(memberRepository.findMemberByLoginId("yi")).thenReturn(Optional.of(leader));
        when(memberRepository.findMemberByLoginId("one")).thenReturn(Optional.of(member1));
        when(memberRepository.findMemberByLoginId("two")).thenReturn(Optional.of(member2));
        when(contestRepository.findById(contestId)).thenReturn(Optional.of(contest));
        when(memberTeamRepository.existsByMember(any())).thenReturn(false);
        when(teamNumberRepository.getLockedNumberForContest(contestId)).thenReturn(Optional.of(teamNumber));

        // when
        Long createdTeamId = teamService.createTeam(leaderId, dto);

        // then
        verify(teamNumberRepository).getLockedNumberForContest(contestId);

        ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(teamCaptor.capture());
        Team savedTeam = teamCaptor.getValue();

        //팀 번호가 003인지 확인
        assertEquals("003", savedTeam.getTeamNumber());
        //팀 생성 반환 확인
        assertEquals("팀입니다", savedTeam.getName());
        assertEquals(contest, savedTeam.getContest());
        assertEquals(leader, savedTeam.getLeader());
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

        when(memberRepository.findMemberByLoginId(loginId)).thenReturn(Optional.of(member));
        when(teamRepository.findTeamByMemberLoginId(loginId)).thenReturn(List.of(team1, team2));

        // when
        List<MyTeamInfoDto> result = teamService.getMyTeamInfo(loginId);

        // then
        assertThat(result.get(0).getTeamName()).isEqualTo("one");
        assertThat(result.get(1).getTeamName()).isEqualTo("two");
    }

    @DisplayName("특정 대회에 자신이 참여한 팀을 단건 조회할 수 있다.")
    @Test
    void getMyTeamInfoByContest(){
        // given
        String loginId = "yi";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("사람 이름")
                .organization(school)
                .build();
        memberRepository.save(member);

        Contest contest = Contest.builder()
                .title("테스트대회")
                .season(2025)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        Team team = Team.builder().name("이팀").winner(false).leader(member).teamNumber("003").contest(contest).build();
        teamRepository.save(team);

        String loginId2 = "kim";
        Address address2 = new Address("street","city","zipCode","detail");
        School school2 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member2 = Member.builder()
                .loginId(loginId2)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email2@email.com")
                .address(address2)
                .gender(Gender.MAN)
                .phoneNumber("01012341235")
                .name("사람 이름2")
                .organization(school2)
                .build();
        memberRepository.save(member2);

        memberTeamRepository.save(MemberTeam.of(member, team));
        memberTeamRepository.save(MemberTeam.of(member2, team));

        // when
        MyTeamInfoByContestDto myTeamInfoByContestDto = teamService.getMyTeamInfoByContest(contest.getId(), member.getLoginId());

        // then
        assertThat(myTeamInfoByContestDto.getTeamId()).isEqualTo(team.getId());
        assertThat(myTeamInfoByContestDto.getTeamName()).isEqualTo("이팀");
        assertThat(myTeamInfoByContestDto.getLeader().getLoginId()).isEqualTo("yi");
        assertThat(myTeamInfoByContestDto.getMemberIds()).containsExactlyInAnyOrder("yi", "kim");
        assertThat(myTeamInfoByContestDto.getCreatedAt()).isNotNull();
    }
}