package com.example.cpsplatform.teamnumber.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.example.cpsplatform.certificate.repository.CertificateRepository;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.team.service.TeamService;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import com.example.cpsplatform.teamnumber.domain.TeamNumber;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class TeamNumberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private MemberTeamRepository memberTeamRepository;

    @Autowired
    private TeamNumberRepository teamNumberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CertificateRepository certificateRepository;

    private Long contestId;

    @BeforeEach
    void init() {
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(2025)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);
        contestId = contest.getId();

        TeamNumber teamNumber = TeamNumber.of(contest, 0);
        teamNumberRepository.save(teamNumber);
    }

    @AfterEach
    void tearDown(){
        memberTeamRepository.deleteAllInBatch();
        teamNumberRepository.deleteAllInBatch();
        memberTeamRepository.deleteAllInBatch();
        certificateRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        contestRepository.deleteAllInBatch();

    }

    @Test
    @DisplayName("5명이 동시에 팀을 생성해도 teamNumber가 중복되지 않는다")
    void createTeamConcurrent() throws InterruptedException {
        // given
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    String loginId = "leader" + idx;
                    Address address = new Address("street", "city", "zipCode", "detail");
                    School school = new School("xx대학교", StudentType.COLLEGE, 4);
                    Member member = Member.builder()
                            .loginId(loginId)
                            .password(passwordEncoder.encode("password"))
                            .role(Role.USER)
                            .birth(LocalDate.now())
                            .email("email" + idx + "@email.com")
                            .address(address)
                            .gender(Gender.MAN)
                            .phoneNumber("0101234123" + idx)
                            .name("팀장 이름")
                            .organization(school)
                            .build();
                    memberRepository.save(member);

                    String memberLoginId = "member" + idx;
                    Address address1 = new Address("street", "city", "zipCode", "detail");
                    School school1 = new School("xx대학교", StudentType.COLLEGE, 4);
                    Member teamMember = Member.builder()
                            .loginId(memberLoginId)
                            .password(passwordEncoder.encode("password"))
                            .role(Role.USER)
                            .birth(LocalDate.now())
                            .email("memberEmail" + idx + "@email.com")
                            .address(address1)
                            .gender(Gender.MAN)
                            .phoneNumber("0109876543" + idx)
                            .name("팀원 이름")
                            .organization(school1)
                            .build();
                    memberRepository.save(teamMember);

                    TeamCreateDto createDto = new TeamCreateDto("테스트" + idx, contestId, List.of(memberLoginId));
                    teamService.createTeam(loginId, createDto);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Long totalCountTeamNumbers = teamRepository.findAll().stream()
                .map(Team::getTeamNumber)
                .distinct()
                .count();

        assertThat(totalCountTeamNumbers).isEqualTo(threadCount);
    }
}