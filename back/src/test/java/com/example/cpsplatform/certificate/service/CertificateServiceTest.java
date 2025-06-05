package com.example.cpsplatform.certificate.service;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.CertificateRepository;
import com.example.cpsplatform.certificate.service.dto.DownloadCertificateResult;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.team.domain.Division;
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.template.exporter.CertificateExporter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class CertificateServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    Map<CertificateType, CertificateExporter> certificateExporterMap;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    CertificateService certificateService;


    @DisplayName("확인증에 타입에 맞게 확인증을 렌더링하고 파일로 다운로드 받는다.")
    @Test
    void downloadCertificate(){
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(loginId)
                .password("1234")
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("Certificate@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01011112222")
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

        Team team = Team.builder()
                .name("one")
                .winner(false)
                .teamNumber("001")
                .leader(member)
                .status(SubmitStatus.NOT_SUBMITTED)
                .division(Division.COLLEGE_GENERAL)
                .contest(contest)
                .build();
        teamRepository.save(team);

        Certificate certificate = Certificate.builder()
                .certificateType(CertificateType.PRELIMINARY)
                .title("16회 창의력 경진대회 예선 참가확인서")
                .serialNumber(UUID.randomUUID().toString())
                .team(team)
                .member(member)
                .contest(contest)
                .build();

        certificateRepository.save(certificate);
        //when
        //then
        assertDoesNotThrow(() -> certificateService.downloadCertificate(member.getLoginId(), certificate.getId()));
    }

    @DisplayName("확인증이 존재하지 않을 경우 예외가 발생한다.")
    @Test
    void downloadCertificateWithNotCertificate(){
        //given
        String loginId = "loginId";
        Long invalidCertificateId = 999L;

        //when
        //then
        assertThatThrownBy(() -> certificateService.downloadCertificate(loginId, invalidCertificateId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 확인증은 존재하지 않습니다.");
    }

    @DisplayName("확인증에 타입에 맞게 확인증을 렌더링하고 파일로 다운로드 받는다.")
    @Test
    void downloadCertificateWithNotOwner(){
        //given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId(loginId)
                .password("1234")
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("Certificate@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01011112222")
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

        Team team = Team.builder()
                .name("one")
                .winner(false)
                .teamNumber("001")
                .leader(member)
                .status(SubmitStatus.NOT_SUBMITTED)
                .division(Division.COLLEGE_GENERAL)
                .contest(contest)
                .build();
        teamRepository.save(team);

        Certificate certificate = Certificate.builder()
                .certificateType(CertificateType.PRELIMINARY)
                .title("16회 창의력 경진대회 예선 참가확인서")
                .serialNumber(UUID.randomUUID().toString())
                .team(team)
                .member(member)
                .contest(contest)
                .build();

        certificateRepository.save(certificate);

        String invalidLoginId = "invalidId";

        //when
        //then
        assertThatThrownBy(() -> certificateService.downloadCertificate(invalidLoginId, certificate.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본인의 확인증만 다운로드할 수 있습니다.");
    }
}