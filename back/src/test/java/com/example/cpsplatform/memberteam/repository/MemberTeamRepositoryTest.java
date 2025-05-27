package com.example.cpsplatform.memberteam.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Transactional
class MemberTeamRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberTeamRepository memberTeamRepository;

    @BeforeEach
    void setUp(){
        memberTeamRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("팀에 소속된 모든 회원을 삭제할 수 있다.")
    @Test
    void deleteAllByTeam() {
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

        Contest contest = Contest.builder()
                .title("테스트대회")
                .season(2025)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        Team team = Team.builder()
                .name("one")
                .status(SubmitStatus.NOT_SUBMITTED)
                .winner(false)
                .teamNumber("001")
                .leader(member)
                .contest(contest)
                .build();
        teamRepository.save(team);

        memberTeamRepository.save(MemberTeam.of(member, team));
        memberTeamRepository.save(MemberTeam.of(member2, team));

        // when
        memberTeamRepository.deleteAllByTeam(team);

        // then
        List<MemberTeam> memberTeams = memberTeamRepository.findAll();
        assertThat(memberTeams).isEmpty();
    }

    @DisplayName("팀에서 리더를 제외하고 남은 팀원을 전부 삭제할 수 있다.")
    @Test
    void deleteAllByTeamExceptLeader() {
        // given
        String loginId = "yi";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
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
        memberRepository.save(leader);

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

        Contest contest = Contest.builder()
                .title("테스트대회")
                .season(2025)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        Team team = Team.builder()
                .name("one")
                .winner(false)
                .teamNumber("001")
                .leader(leader)
                .contest(contest)
                .status(SubmitStatus.NOT_SUBMITTED)
                .build();
        teamRepository.save(team);

        memberTeamRepository.save(MemberTeam.of(leader, team));
        memberTeamRepository.save(MemberTeam.of(member2, team));

        // when
        memberTeamRepository.deleteAllByTeamExceptLeader(team, leader);

        // then
        List<MemberTeam> memberTeams = memberTeamRepository.findAll();
        assertThat(memberTeams.get(0).getMember()).isEqualTo(leader);
    }

    @DisplayName("유저의 아이디와 해당 대회 id로 소속된 팀이 있는지 확인한다.")
    @Test
    void existsByContestIdAndLoginId(){
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
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

        String loginId2 = "loginId2";
        Address address2 = new Address("street","city","zipCode","detail");
        School school2 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member2 = Member.builder()
                .loginId(loginId2)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email2@email.com")
                .address(address2)
                .gender(Gender.WOMAN)
                .phoneNumber("01012341235")
                .name("사람 이름2")
                .organization(school2)
                .build();
        memberRepository.saveAll(List.of(leader,member2));

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(16)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        Contest anotherContest = Contest.builder()
                .title("테스트 대회")
                .season(17)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.saveAll(List.of(contest, anotherContest));

        Team team = Team.builder()
                .name("팀 이름")
                .winner(false)
                .leader(leader)
                .teamNumber("001")
                .status(SubmitStatus.TEMPORARY)
                .contest(contest)
                .build();

        teamRepository.save(team);

        MemberTeam memberTeam = MemberTeam.builder()
                .member(member2)
                .team(team)
                .build();
        MemberTeam leaderTeam = MemberTeam.builder()
                .member(leader)
                .team(team)
                .build();

        memberTeamRepository.saveAll(List.of(memberTeam,leaderTeam));

        //when
        boolean result = memberTeamRepository.existsByContestIdAndLoginId(contest.getId(), member2.getLoginId());

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("리더로 대회를 참여한 유저의 아이디와 해당 대회 id로 소속된 팀이 있는지 확인한다.")
    @Test
    void existsByContestIdAndLoginIdWithLeader(){
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
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

        memberRepository.save(leader);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(16)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        Contest anotherContest = Contest.builder()
                .title("테스트 대회")
                .season(17)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.saveAll(List.of(contest, anotherContest));

        Team team = Team.builder()
                .name("팀 이름")
                .winner(false)
                .leader(leader)
                .teamNumber("001")
                .contest(contest)
                .status(SubmitStatus.NOT_SUBMITTED)
                .build();
        teamRepository.save(team);

        MemberTeam leaderTeam = MemberTeam.builder()
                .member(leader)
                .team(team)
                .build();

        memberTeamRepository.save(leaderTeam);

        //when
        boolean result = memberTeamRepository.existsByContestIdAndLoginId(contest.getId(), leader.getLoginId());

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("해당 대회에 소속된 팀이 없다면 False 반환한다.")
    @Test
    void existsByContestIdAndLoginIdWithAnotherContest(){
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
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

        memberRepository.save(leader);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(16)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        Contest anotherContest = Contest.builder()
                .title("테스트 대회")
                .season(17)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.saveAll(List.of(contest, anotherContest));

        Team team = Team.builder()
                .name("팀 이름")
                .winner(false)
                .leader(leader)
                .teamNumber("001")
                .contest(contest)
                .status(SubmitStatus.NOT_SUBMITTED)
                .build();
        teamRepository.save(team);
        //when
        boolean result = memberTeamRepository.existsByContestIdAndLoginId(anotherContest.getId(), leader.getLoginId());

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("해당 대회 id로 참가한 팀과 유저의 정보를 페치 조인해서 함께 조회한다.")
    @Test
    void findAllByContestId(){
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
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
        memberRepository.save(leader);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(16)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        Team team = Team.builder()
                .name("팀 이름")
                .winner(false)
                .leader(leader)
                .teamNumber("001")
                .contest(contest)
                .status(SubmitStatus.TEMPORARY)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam = MemberTeam.builder()
                .team(team)
                .member(leader)
                .build();
        memberTeamRepository.save(memberTeam);
        //when
        List<MemberTeam> memberTeamList = memberTeamRepository.findAllByContestId(contest.getId());
        //then
        assertThat(memberTeamList).hasSize(1)
                .extracting("member","team")
                .containsExactly(tuple(leader,team));
    }

    @DisplayName("유저의 아이디와 팀의 id를 받아 유저가 팀에 속해있는지 확인한다.")
    @Test
    void existsByTeamIdAndLoginId(){
        //given

        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
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
        memberRepository.save(leader);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(16)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        Team team = Team.builder()
                .name("팀 이름")
                .winner(false)
                .leader(leader)
                .teamNumber("001")
                .contest(contest)
                .status(SubmitStatus.NOT_SUBMITTED)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam = MemberTeam.builder()
                .team(team)
                .member(leader)
                .build();

        memberTeamRepository.save(memberTeam);

        //when
        boolean result = memberTeamRepository.existsByTeamIdAndLoginId(team.getId(), leader.getLoginId());
        //then
        Assertions.assertThat(result).isTrue();
    }

    @DisplayName("유저의 아이디와 팀의 id를 받아 유저가 팀에 속해있는지 확인한다.")
    @Test
    void existsByTeamIdAndLoginIdWithNotExistMember(){
        //given
        //when
        //유효하지않은 팀 id와 유효하지 않은 아이디조 조회
        boolean result = memberTeamRepository.existsByTeamIdAndLoginId(999L, "invalidLoginId");
        //then
        Assertions.assertThat(result).isFalse();
    }
}