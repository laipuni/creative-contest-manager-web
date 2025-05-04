package com.example.cpsplatform.team.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.example.cpsplatform.auth.email.EmailService;
import com.example.cpsplatform.auth.email.config.EmailConfig;
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
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.team.domain.Team;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class TeamRepositoryTest {

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

    @DisplayName("해당 로그인아이디로 소속된 팀들을 조회할 수 있다.")
    @Test
    void findTeamByMemberLoginId(){
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

        Team team1 = Team.builder().name("one").winner(false).teamNumber("001").leader(member).contest(contest).build();
        Team team2 = Team.builder().name("two").winner(false).teamNumber("002").leader(member).contest(contest).build();
        teamRepository.saveAll(List.of(team1, team2));

        memberTeamRepository.save(MemberTeam.of(member, team1));
        memberTeamRepository.save(MemberTeam.of(member, team2));

        // when
        List<Team> result = teamRepository.findTeamByMemberLoginId("yi");

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("name").containsExactlyInAnyOrder("one", "two");
        assertThat(result).allSatisfy(team -> {
            assertThat(team.getLeader().getLoginId()).isEqualTo("yi");
        });
    }

    @DisplayName("해당 대회에 팀장으로 참여한 팀이 있는지 조회한다.")
    @Test
    void existsTeamByContestIdAndLeaderId(){
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("이름")
                .organization(school)
                .build();

        memberRepository.save(member);

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(16)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        Team team = Team.builder().name("팀 이름").winner(false).teamNumber("001").leader(member).contest(contest).build();
        teamRepository.save(team);
        //when
        Team result = teamRepository.findTeamByContestIdAndLeaderId(contest.getId(), member.getLoginId()).get();
        //then
        assertThat(result).isNotNull()
                .extracting("name","winner","leader","contest")
                .containsExactly(team.getName(),team.getWinner(),team.getLeader(),team.getContest());
    }
}