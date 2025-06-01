package com.example.cpsplatform.certificate.admin.service;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
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
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.team.domain.Division;
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class CertificateAdminServiceTest {

    @Autowired
    EntityManager entityManager;

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

    @Autowired
    CertificateAdminService certificateAdminService;

    @Autowired
    CertificateRepository certificateRepository;

    @DisplayName("예선 참여 확인증 일괄 생성할 때, 존재하지 않는 대회일 경우 예외가 발생한다.")
    @Test
    void batchCreatePreliminaryCertificatesWithInvalidContestId() {
        //given
        Long invalidContestId = 999L;

        //when
        //then
        assertThatThrownBy(() -> certificateAdminService.batchCreatePreliminaryCertificates(invalidContestId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 대회는 존재하지 않습니다.");
    }

    @DisplayName("예선 참여 확인증 일괄 생성할 때, 참가 팀이 없는 대회일 경우 확인증이 생성되지 않는다")
    @Test
    void batchCreatePreliminaryCertificatesWithoutMemberTeams() {
        //given
        Contest contest = Contest.builder()
                .title("빈 대회")
                .season(1)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(1).atStartOfDay())
                .startTime(LocalDate.now().plusDays(2).atStartOfDay())
                .endTime(LocalDate.now().plusDays(3).atStartOfDay())
                .build();
        contestRepository.save(contest);

        //when
        certificateAdminService.batchCreatePreliminaryCertificates(contest.getId());

        //then
        List<Certificate> result = certificateRepository.findAll();
        assertThat(result).isEmpty();
    }



    @DisplayName("해당 예선 대회에 참여한 팀에 유저들의 예선 참여 확인증 일괄 생성한다.")
    @Test
    void batchCreatePreliminaryCertificates(){
        //given
        Address address1 = new Address("street","city","zipCode","detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
                .loginId("leaderId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("leaderemail@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01078451456")
                .name("리더")
                .organization(school1)
                .build();

        Address address2 = new Address("street","city","zipCode","detail");
        School school2 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId("memberId")
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("memberemail@email.com")
                .address(address2)
                .gender(Gender.WOMAN)
                .phoneNumber("01012345678")
                .name("유저")
                .organization(school2)
                .build();
        memberRepository.saveAll(List.of(leader,member));

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
                .status(SubmitStatus.NOT_SUBMITTED)
                .teamNumber("001")
                .division(Division.COLLEGE_GENERAL)
                .contest(contest)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam1 = MemberTeam.builder()
                .team(team)
                .member(leader)
                .build();
        MemberTeam memberTeam2 = MemberTeam.builder()
                .team(team)
                .member(member)
                .build();
        memberTeamRepository.saveAll(List.of(memberTeam1,memberTeam2));
        entityManager.flush();
        entityManager.clear();

        //when
        certificateAdminService.batchCreatePreliminaryCertificates(contest.getId());
        //then
        List<Certificate> result = certificateRepository.findAll();
        assertThat(result).hasSize(2);

    }

    @DisplayName("확인증 id를 받아 해당 확인증을 삭제한다.")
    @Test
    void deleteCertificate(){
        //given
        Address address1 = new Address("street","city","zipCode","detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
                .loginId("leaderId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("leaderemail@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01045617896")
                .name("리더")
                .organization(school1)
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
                .status(SubmitStatus.NOT_SUBMITTED)
                .division(Division.COLLEGE_GENERAL)
                .contest(contest)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam1 = MemberTeam.builder()
                .team(team)
                .member(leader)
                .build();
        memberTeamRepository.save(memberTeam1);
        Certificate certificate = Certificate.builder()
                .serialNumber(UUID.randomUUID().toString())
                .title("16회 예선 참가 확인증")
                .certificateType(CertificateType.PRELIMINARY)
                .contest(contest)
                .member(leader)
                .team(team)
                .build();
        certificateRepository.save(certificate);
        entityManager.flush();
        entityManager.clear();

        //when
        certificateAdminService.deleteCertificate(certificate.getId());
        //then
        List<Certificate> result = certificateRepository.findAll();
        assertThat(result).isEmpty();

    }

}