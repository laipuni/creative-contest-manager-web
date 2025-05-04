package com.example.cpsplatform.contest.admin.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.admin.request.DeleteContestRequest;
import com.example.cpsplatform.contest.admin.request.UpdateContestRequest;
import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestDeleteDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestUpdateDto;
import com.example.cpsplatform.contest.admin.service.dto.WinnerTeamsDto;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.DuplicateDataException;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ContestAdminServiceTest {

    @Autowired
    ContestAdminService contestAdminService;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EntityManager entityManager;

    @Transactional
    @DisplayName("대회의 정보를 받아서 대회를 생성 및 저장한다.")
    @Test
    void createContest(){
        //given
        String title = "title";
        int season = 1;
        String description ="대회 설명";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        ContestCreateDto contestCreateDto = new ContestCreateDto(title,season,description,
                registrationStartAt,registrationEndAt,contestStartAt,contestEndAt);
        //when
        contestAdminService.createContest(contestCreateDto);
        List<Contest> result = contestRepository.findAll();
        //then
        assertThat(result.get(0))
                .extracting("title", "season", "description", "registrationStartAt", "registrationEndAt",
                        "startTime","endTime")
                .containsExactly(title,season,description,registrationStartAt,
                        registrationEndAt,contestStartAt,contestEndAt);
    }

    @DisplayName("수정할 대회가 존재하지 않으면 예외가 발생한다.")
    @Test
    void updateContestWithNotExistContest(){
        //given
        Long invalidContestId = 2025L;
        String title = "title";
        int season = 1;
        String description ="대회 설명";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        ContestUpdateDto updateDto = new ContestUpdateDto(invalidContestId,title,season,description,
                registrationStartAt,registrationEndAt,contestStartAt,contestEndAt);
        //when
        //then
        assertThatThrownBy(() -> contestAdminService.updateContest(updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("수정할 대회가 존재하지 않습니다.");
    }

    @Transactional
    @DisplayName("수정할 대회가 존재하지 않으면 예외가 발생한다.")
    @Test
    void updateContest(){
        //given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        //contest 생성
        Contest contest = Contest.builder()
                .title("title")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();

        contestRepository.save(contest);

        //수정할 내용 선언
        String updatedTitle = "updatedTitle";
        int updatedSeason = 2;
        String updatedDescription ="수정된 대회 설명";
        LocalDateTime updatedRegistrationStartAt = now.plusDays(2);
        LocalDateTime updatedRegistrationEndAt= now.plusDays(3);
        LocalDateTime updatedContestStartAt = now.plusDays(4);
        LocalDateTime updatedContestEndAt = now.plusDays(5);

        ContestUpdateDto updateDto = new ContestUpdateDto(contest.getId(),updatedTitle,updatedSeason,updatedDescription,
                updatedRegistrationStartAt,updatedRegistrationEndAt,updatedContestStartAt,updatedContestEndAt);
        //when
        contestAdminService.updateContest(updateDto);
        List<Contest> result = contestRepository.findAll();
        //then
        assertThat(result.get(0))
                .extracting("title", "season", "description", "registrationStartAt", "registrationEndAt",
                        "startTime","endTime")
                .containsExactly(updatedTitle,updatedSeason,updatedDescription,updatedRegistrationStartAt,
                        updatedRegistrationEndAt,updatedContestStartAt,updatedContestEndAt);
    }

    @Transactional
    @DisplayName("수정할 대회가 존재하지 않으면 예외가 발생한다.")
    @Test
    void deleteContest(){
        //given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        //contest 생성
        Contest contest = Contest.builder()
                .title("title")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();

        contestRepository.save(contest);

        ContestDeleteDto request = new ContestDeleteDto(contest.getId());

        //when
        contestAdminService.deleteContest(request);
        List<Contest> result = contestRepository.findAll();
        //then
        assertThat(result).hasSize(0);
    }

    @Transactional
    @DisplayName("우승팀을 지정하면 해당 팀들의 winner가 true로 변경된다.")
    @Test
    void selectWinnerTeams(){
        //given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(1)
                .registrationStartAt(now.minusDays(5))
                .registrationEndAt(now.minusDays(2))
                .startTime(now.minusHours(1))
                .endTime(now.plusHours(2))
                .build();
        contestRepository.save(contest);

        String loginId = "leaderId";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx초등학교", StudentType.ELEMENTARY, 4);
        Member leader = Member.builder()
                .loginId(loginId)
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

        String loginId1 = "leaderId1";
        Address address1 = new Address("street", "city", "zipCode", "detail");
        School school1 = new School("oo초등학교", StudentType.ELEMENTARY, 4);
        Member leader1 = Member.builder()
                .loginId(loginId1)
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("email1@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01012341235")
                .name("팀장")
                .organization(school1)
                .build();
        memberRepository.save(leader1);

        Team team = Team.builder()
                .name("테스트팀")
                .winner(false)
                .leader(leader)
                .contest(contest)
                .teamNumber("003")
                .section(Section.ELEMENTARY_MIDDLE)
                .build();
        teamRepository.save(team);

        Team team1 = Team.builder()
                .name("테스트팀1")
                .winner(false)
                .leader(leader1)
                .contest(contest)
                .teamNumber("004")
                .section(Section.ELEMENTARY_MIDDLE)
                .build();
        teamRepository.save(team1);

        List<Long> winnerTeamIds = List.of(team.getId(), team1.getId());
        WinnerTeamsDto dto = new WinnerTeamsDto(winnerTeamIds);

        // when
        contestAdminService.selectWinnerTeams(contest.getId(), dto);

        // then
        List<Team> result = teamRepository.findAllById(winnerTeamIds);
        assertThat(result).extracting(Team::getWinner).containsOnly(true);
    }
}