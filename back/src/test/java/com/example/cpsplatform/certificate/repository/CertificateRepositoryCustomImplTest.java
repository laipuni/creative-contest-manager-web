package com.example.cpsplatform.certificate.repository;

import com.example.cpsplatform.certificate.controller.response.SearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.service.CertificateService;
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
import com.example.cpsplatform.team.repository.TeamRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
@SpringBootTest
class CertificateRepositoryCustomImplTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private MemberTeamRepository memberTeamRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("사용자명과 인증서 타입으로 인증서를 검색한다")
    @Test
    void searchCertificateByUsernameAndType() {
        // given
        int page = 0;
        int pageSize = 10;
        String order = "asc";
        CertificateType certificateType = CertificateType.PRELIMINARY;

        Member leader = createAndSaveLeader();
        Contest contest = createAndSaveContest();
        Team team = createAndSaveTeam(leader, contest);
        Certificate certificate1 = createAndSaveCertificate(leader, team, contest, certificateType);
        Certificate certificate2 = createAndSaveCertificate(leader, team, contest, certificateType);
        entityManager.flush();
        entityManager.clear();

        //when
        SearchCertificateResponse response = certificateRepository.SearchCertificate(
                page, pageSize, order, certificateType, leader.getLoginId()
        );

        //then
        assertThat(response).isNotNull();
        assertThat(response.getCertificateDtoList()).hasSize(2);
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(response.getTotalPage()).isEqualTo(1);

        assertThat(response.getCertificateDtoList())
                .extracting("certificateId", "title", "certificateType", "teamName")
                .containsExactlyInAnyOrder(
                        tuple(certificate1.getId(), certificate1.getTitle(), certificateType, team.getName()),
                        tuple(certificate2.getId(), certificate2.getTitle(), certificateType, team.getName())
                );
    }

    @DisplayName("인증서 타입 없이 사용자명으로만 인증서를 검색한다")
    @Test
    void searchCertificateByUsernameOnly() {
        //given
        int page = 0;
        int pageSize = 10;
        String order = "asc";

        Member leader = createAndSaveLeader();
        Contest contest = createAndSaveContest();
        Team team = createAndSaveTeam(leader, contest);

        Certificate preliminaryCertificate = createAndSaveCertificate(leader, team, contest, CertificateType.PRELIMINARY);
        Certificate finalCertificate = createAndSaveCertificate(leader, team, contest, CertificateType.FINAL);

        entityManager.flush();
        entityManager.clear();

        //when
        SearchCertificateResponse response = certificateRepository.SearchCertificate(
                page, pageSize, order, null, leader.getLoginId()
        );

        //then
        assertThat(response).isNotNull();
        assertThat(response.getCertificateDtoList()).hasSize(2);
        assertThat(response.getSize()).isEqualTo(2);

        assertThat(response.getCertificateDtoList())
                .extracting("certificateId", "title", "certificateType", "teamName")
                .containsExactlyInAnyOrder(
                        tuple(preliminaryCertificate.getId(), preliminaryCertificate.getTitle(), CertificateType.PRELIMINARY, team.getName()),
                        tuple(finalCertificate.getId(), finalCertificate.getTitle(), CertificateType.FINAL, team.getName())
                );
    }

    @DisplayName("페이지당 항목 수를 설정하여 인증서를 검색한다")
    @Test
    void searchCertificateWithPagination() {
        //given
        int page = 0;
        int pageSize = 1;
        String order = "asc";

        Member leader = createAndSaveLeader();
        Contest contest = createAndSaveContest();
        Team team = createAndSaveTeam(leader, contest);

        createAndSaveCertificate(leader, team, contest, CertificateType.PRELIMINARY);
        createAndSaveCertificate(leader, team, contest, CertificateType.FINAL);
        createAndSaveCertificate(leader, team, contest, CertificateType.PRELIMINARY);

        entityManager.flush();
        entityManager.clear();

        //when
        SearchCertificateResponse firstPageResponse = certificateRepository.SearchCertificate(
                page, pageSize, order, null, leader.getLoginId()
        );

        //then
        assertThat(firstPageResponse).isNotNull();
        assertThat(firstPageResponse.getCertificateDtoList()).hasSize(1);
        assertThat(firstPageResponse.getTotalPage()).isEqualTo(3);
    }

    private Member createAndSaveLeader() {
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        Member leader = Member.builder()
                .loginId("leaderId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("leaderemail@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012341234")
                .name("리더")
                .organization(school)
                .build();
        return memberRepository.save(leader);
    }

    private Contest createAndSaveContest() {
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(16)
                .registrationStartAt(LocalDateTime.now().plusDays(3))
                .registrationEndAt(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now().plusDays(10))
                .endTime(LocalDateTime.now().plusDays(10).plusHours(1))
                .build();
        return contestRepository.save(contest);
    }

    private Team createAndSaveTeam(Member leader, Contest contest) {
        Team team = Team.builder()
                .name("팀 이름")
                .winner(false)
                .leader(leader)
                .teamNumber("001")
                .contest(contest)
                .build();
        Team savedTeam = teamRepository.save(team);

        MemberTeam memberTeam = MemberTeam.builder()
                .team(savedTeam)
                .member(leader)
                .build();
        memberTeamRepository.save(memberTeam);

        return savedTeam;
    }

    private Certificate createAndSaveCertificate(Member member, Team team, Contest contest, CertificateType certificateType) {
        Certificate certificate = Certificate.builder()
                .title("16회 참여 확인 증명서")
                .serialNumber(UUID.randomUUID().toString())
                .certificateType(certificateType)
                .team(team)
                .member(member)
                .contest(contest)
                .build();
        return certificateRepository.save(certificate);
    }

}