package com.example.cpsplatform.contest.controller.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.contest.service.ContestJoinService;
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
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
class ContestJoinServiceTest {

    @Autowired
    private ContestJoinService contestService;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberTeamRepository memberTeamRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("사용자가 대회에 성공적으로 참가한다")
    @Test
    void join_Success() {
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        LocalDateTime now = LocalDateTime.now();
        Member leader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.of(2003,1,1))
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
                .registrationStartAt(now.minusDays(6))
                .registrationEndAt(now.minusDays(5))
                .startTime(now.minusHours(1))
                .endTime(now.plusHours(1)) //테스트 시점의 시간 -1 ~ +1 시간으로 설정
                .build();
        contestRepository.save(contest);

        Team team = Team.builder()
                .name("테스트 팀")
                .winner(false)
                .leader(leader)
                .contest(contest)
                .build();

        teamRepository.save(team);

        //사용자를 팀에 추가
        MemberTeam memberTeam = MemberTeam.builder()
                .member(leader)
                .team(team)
                .build();

        memberTeamRepository.save(memberTeam);

        //when
        //then
        assertThatCode(() -> contestService.validateContestParticipation(contest.getId(), loginId, now))
                .doesNotThrowAnyException();
    }

    @DisplayName("사용자가 대회에 소속된 팀이 없으면 예외가 발생한다")
    @Test
    void join_TeamNotFound() {
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.of(2003,1,1))
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("사람 이름")
                .organization(school)
                .build();
        memberRepository.save(leader);

        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("테스트 대회 설명")
                .season(1)
                .registrationStartAt(now.minusDays(5))
                .registrationEndAt(now.minusDays(2))
                .startTime(now.minusHours(1))
                .endTime(now.plusHours(2))
                .build();

        contestRepository.save(contest);

        //when
        //then
        assertThatThrownBy(() -> contestService.validateContestParticipation(contest.getId(), loginId, now))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageContaining("제"+ contest.getSeason()+"회 대회에 소속된 팀이 없습니다.");
    }

    @DisplayName("대회가 진행 중이 아니면 예외가 발생한다")
    @Test
    void join_ContestNotOngoing() {
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        LocalDateTime now = LocalDateTime.now();
        Member leader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.of(2003,1,1))
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
                .registrationStartAt(now.minusDays(9))
                .registrationEndAt(now.minusDays(8))
                .startTime(now.minusDays(7))
                .endTime(now.minusDays(6)) //대회가 끝난 시간으로 설정
                .build();
        contestRepository.save(contest);

        Team team = Team.builder()
                .name("테스트 팀")
                .winner(false)
                .leader(leader)
                .contest(contest)
                .build();

        teamRepository.save(team);

        //사용자를 팀에 추가
        MemberTeam memberTeam = MemberTeam.builder()
                .member(leader)
                .team(team)
                .build();

        memberTeamRepository.save(memberTeam);

        //when
        //then
        assertThatThrownBy(() -> contestService.validateContestParticipation(contest.getId(), loginId, now))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageContaining("제"+ contest.getSeason()+"회 대회는 현재 개최 기간이 아닙니다.");
    }

    @DisplayName("존재하지 않는 대회에 참가하려고 하면 예외가 발생한다")
    @Test
    void join_ContestNotFound() {
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.of(2003,1,1))
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("사람 이름")
                .organization(school)
                .build();
        memberRepository.save(leader);

        Long invalidContestId = 9999L;
        LocalDateTime now = LocalDateTime.now();

        //when
        //then
        assertThatThrownBy(() -> contestService.validateContestParticipation(invalidContestId, loginId, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 대회는 존재하지 않습니다");
    }
}