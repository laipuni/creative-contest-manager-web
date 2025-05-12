package com.example.cpsplatform.team.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.ContestJoinException;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.security.encoder.CryptoService;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.team.service.dto.MyTeamInfoByContestDto;
import com.example.cpsplatform.team.service.dto.MyTeamInfoDto;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import com.example.cpsplatform.team.service.dto.TeamUpdateDto;
import com.example.cpsplatform.teamnumber.repository.TeamNumberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class TeamServiceTest {
    @Autowired
    private TeamService teamService;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private ContestRepository contestRepository;

    @MockitoBean
    private TeamRepository teamRepository;

    @MockitoBean
    private TeamNumberRepository teamNumberRepository;

    @MockitoBean
    private MemberTeamRepository memberTeamRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CryptoService cryptoService;


    @DisplayName("팀원이 3명 초과할 경우 예외가 발생한다.")
    @Test
    void createTeamOverMember() {
        // given
        String leaderId = "yi";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        Member leader = Member.builder()
                .loginId(leaderId)
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("팀장")
                .organization(school)
                .build();
        memberRepository.save(leader);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(2025)
                .registrationStartAt(LocalDateTime.now())
                .registrationEndAt(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
        contestRepository.save(contest);

        Address address1 = new Address("street", "city", "zipCode", "detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE, 4);
        Member member1 = Member.builder()
                .loginId("one")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email1@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01012341235")
                .name("팀원1")
                .organization(school1)
                .build();
        memberRepository.save(member1);

        Address address2 = new Address("street", "city", "zipCode", "detail");
        School school2 = new School("xx대학교", StudentType.COLLEGE, 4);
        Member member2 = Member.builder()
                .loginId("two")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email2@email.com")
                .address(address2)
                .gender(Gender.MAN)
                .phoneNumber("01012341236")
                .name("팀원2")
                .organization(school2)
                .build();
        memberRepository.save(member2);

        Address address3 = new Address("street", "city", "zipCode", "detail");
        School school3 = new School("xx대학교", StudentType.COLLEGE, 4);
        Member member3 = Member.builder()
                .loginId("three")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email3@email.com")
                .address(address3)
                .gender(Gender.MAN)
                .phoneNumber("01012341237")
                .name("팀원3")
                .organization(school3)
                .build();
        memberRepository.save(member3);

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

        String leaderId = "진짜리더";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        Member leader = Member.builder()
                .loginId(leaderId)
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("팀장")
                .organization(school)
                .build();
        memberRepository.save(leader);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(2025)
                .registrationStartAt(LocalDateTime.now())
                .registrationEndAt(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
        contestRepository.save(contest);

        Team team = Team.builder()
                .name("테스트팀")
                .winner(false)
                .leader(leader)
                .contest(contest)
                .teamNumber("003")
                .section(Section.HIGH_NORMAL)
                .build();
        teamRepository.save(team);

        //팀명 수정만 원할 경우에도 기존 팀원은 입력해야되는구조
        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> teamService.updateTeam(team.getId(), new TeamUpdateDto("팀명수정", List.of(),contest.getId()), notLeaderId));
    }

    @DisplayName("팀장이 아닌 자가 팀을 삭제하면 예외가 발생한다.")
    @Test
    void deleteTeam() {
        // given
        String notLeaderId = "가짜리더";

        String leaderId = "진짜리더";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        Member leader = Member.builder()
                .loginId(leaderId)
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("팀장")
                .organization(school)
                .build();
        memberRepository.save(leader);
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(2025)
                .registrationStartAt(LocalDateTime.now())
                .registrationEndAt(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
        contestRepository.save(contest);
        Team team = Team.builder()
                .name("테스트팀")
                .winner(false)
                .leader(leader)
                .contest(contest)
                .teamNumber("003")
                .section(Section.HIGH_NORMAL)
                .build();
        teamRepository.save(team);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> teamService.deleteTeam(team.getId(), notLeaderId, contest.getId()));
    }

    @DisplayName("팀을 삭제할 경우 MemberTeam에 있는 관련 내용도 삭제된다.")
    @Test
    void deleteTeamCheckMemberTeam() {
        // given
        String leaderId = "진짜리더";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        Member leader = Member.builder()
                .loginId(leaderId)
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("팀장")
                .organization(school)
                .build();
        memberRepository.save(leader);
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(2025)
                .registrationStartAt(LocalDateTime.now())
                .registrationEndAt(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
        contestRepository.save(contest);
        Team team = Team.builder()
                .name("테스트팀")
                .winner(false)
                .leader(leader)
                .contest(contest)
                .teamNumber("003")
                .section(Section.HIGH_NORMAL)
                .build();
        teamRepository.save(team);

        String loginId1 = "yi";
        Address address1 = new Address("street","city","zipCode","detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(loginId1)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email1@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01012341231")
                .name("사람 이름")
                .organization(school1)
                .build();
        memberRepository.save(member);

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
        teamService.deleteTeam(team.getId(), "진짜리더",contest.getId());

        // then
        assertThat(memberTeamRepository.findAllByTeamId(team.getId())).isEmpty();
    }

    @DisplayName("본인이 소속된 팀 리스트를 성공적으로 반환한다.")
    @Test
    void myTeamInfo() {
        // given
        String loginId1 = "yi";
        Address address1 = new Address("street","city","zipCode","detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(loginId1)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email1@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01012341231")
                .name("사람 이름")
                .organization(school1)
                .build();
        memberRepository.save(member);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(2025)
                .registrationStartAt(LocalDateTime.now())
                .registrationEndAt(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
        contestRepository.save(contest);

        Team team = Team.builder()
                .name("one")
                .winner(false)
                .leader(member)
                .contest(contest)
                .teamNumber("003")
                .section(Section.HIGH_NORMAL)
                .build();
        teamRepository.save(team);

        Contest contest1 = Contest.builder()
                .title("테스트 대회1")
                .season(2024)
                .registrationStartAt(LocalDateTime.now())
                .registrationEndAt(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
        contestRepository.save(contest1);

        Team team1 = Team.builder()
                .name("two")
                .winner(false)
                .leader(member)
                .contest(contest)
                .teamNumber("004")
                .section(Section.HIGH_NORMAL)
                .build();
        teamRepository.save(team1);

        memberTeamRepository.save(MemberTeam.of(member, team));
        memberTeamRepository.save(MemberTeam.of(member, team1));

        // when
        List<MyTeamInfoDto> result = teamService.getMyTeamInfo(loginId1);

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
        assertThat(myTeamInfoByContestDto.getLeaderLoginId()).isEqualTo("yi");
        assertThat(myTeamInfoByContestDto.getMembers())
                .extracting("memberId","loginId","name")
                .containsExactlyInAnyOrder(
                        tuple(member.getId(),member.getLoginId(),member.getName()),
                        tuple(member2.getId(),member2.getLoginId(),member2.getName()));
        assertThat(myTeamInfoByContestDto.getCreatedAt()).isNotNull();
    }

}