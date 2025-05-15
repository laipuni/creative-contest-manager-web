package com.example.cpsplatform.certificate.repository;

import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateDto;
import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateCond;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateDto;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateResponse;
import com.example.cpsplatform.certificate.repository.dto.UserSearchCertificateCond;
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
        CertificateType certificateType_pre = CertificateType.PRELIMINARY;
        CertificateType certificateType_final = CertificateType.FINAL;

        Member leader = createAndSaveLeader();
        Contest contest = createAndSaveContest();
        Team team = createAndSaveTeam(leader, contest);
        Certificate certificate1 = createAndSaveCertificate(leader, team, contest, certificateType_pre);
        Certificate certificate2 = createAndSaveCertificate(leader, team, contest, certificateType_final);
        entityManager.flush();
        entityManager.clear();

        UserSearchCertificateCond cond = new UserSearchCertificateCond(page, pageSize, order, certificateType_pre, leader.getLoginId());

        //when
        UserSearchCertificateResponse response = certificateRepository.SearchUserCertificate(cond);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getCertificateDtoList()).hasSize(1);
        assertThat(response.getSize()).isEqualTo(1);
        assertThat(response.getTotalPage()).isEqualTo(1);

        assertThat(response.getCertificateDtoList())
                .extracting("certificateId", "title", "certificateType", "teamName")
                .containsExactlyInAnyOrder(
                        tuple(certificate1.getId(), certificate1.getTitle(), certificateType_pre, team.getName())

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
        UserSearchCertificateCond cond = new UserSearchCertificateCond(page, pageSize, order, null, leader.getLoginId());
        //when
        UserSearchCertificateResponse response = certificateRepository.SearchUserCertificate(cond);

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

        entityManager.flush();
        entityManager.clear();

        UserSearchCertificateCond cond = new UserSearchCertificateCond(page, pageSize, order, null, leader.getLoginId());

        //when
        UserSearchCertificateResponse firstPageResponse = certificateRepository.SearchUserCertificate(cond);

        //then
        assertThat(firstPageResponse).isNotNull();
        assertThat(firstPageResponse.getCertificateDtoList()).hasSize(1);
        assertThat(firstPageResponse.getTotalPage()).isEqualTo(2);
    }

    @DisplayName("특정 타입의 인증서를 페이지네이션하여 조회할 때 해당 타입의 인증서만 받는다")
    @Test
    void searchAdminCertificatesWithPaginationAndFiltering() {
        //given
        int page = 0;
        int pageSize = 1;
        String orderType = "createdAt";
        String direction = "asc";

        Member leader = createAndSaveLeader();
        Contest contest = createAndSaveContest();
        Team team = createAndSaveTeam(leader, contest);

        //다른 타입 2개의 인증서 생성
        createAndSaveCertificate(leader, team, contest, CertificateType.PRELIMINARY);
        createAndSaveCertificate(leader, team, contest, CertificateType.FINAL);

        entityManager.flush();
        entityManager.clear();

        AdminSearchCertificateCond cond = AdminSearchCertificateCond.builder()
                .page(page)
                .pageSize(pageSize)
                .orderType(orderType)
                .direction(direction)
                .searchType("")
                .keyword("")
                .certificateType(CertificateType.PRELIMINARY)
                .build();

        //when
        AdminSearchCertificateResponse response = certificateRepository.SearchAdminCertificate(cond);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getCertificateDtoList()).hasSize(1);
        assertThat(response.getTotalPage()).isEqualTo(1);
        assertThat(response.getPage()).isEqualTo(page);
        assertThat(response.getSize()).isEqualTo(pageSize);

        AdminSearchCertificateDto certificateDto = response.getCertificateDtoList().get(0);
        assertThat(certificateDto.getCertificateType()).isEqualTo(CertificateType.PRELIMINARY);
        assertThat(certificateDto.getTitle()).isEqualTo("16회 참여 확인 증명서");
        assertThat(certificateDto.getTeamName()).isEqualTo("팀 이름");
    }

    @DisplayName("사용자 로그인 아이디로 인증서를 검색할 때 해당 로그인 아이디의 인증서만 받는다")
    @Test
    void searchAdminCertificatesWithLoginIdSearch() {
        //given
        int page = 0;
        int pageSize = 2;
        String orderType = "createdAt";
        String direction = "desc";

        Member leader1 = createAndSaveLeader("leader1", "김테스트");
        Member leader2 = createAndSaveLeader("leader2", "박테스트");
        Contest contest = createAndSaveContest();
        Team team1 = createAndSaveTeam(leader1, contest, "팀A","001");
        Team team2 = createAndSaveTeam(leader2, contest, "팀B","002");

        //인증서 생성
        createAndSaveCertificate(leader1, team1, contest, CertificateType.PRELIMINARY);
        createAndSaveCertificate(leader2, team2, contest, CertificateType.FINAL);

        entityManager.flush();
        entityManager.clear();

        //조건 설정 (로그인 아이디로 검색)
        AdminSearchCertificateCond cond = AdminSearchCertificateCond.builder()
                .page(page)
                .pageSize(pageSize)
                .orderType(orderType)
                .direction(direction)
                .searchType("loginId")
                .keyword("leader1")
                .build();

        //when
        AdminSearchCertificateResponse response = certificateRepository.SearchAdminCertificate(cond);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getCertificateDtoList()).hasSize(1);
        assertThat(response.getTotalPage()).isEqualTo(1);

        AdminSearchCertificateDto certificateDto = response.getCertificateDtoList().get(0);
        assertThat(certificateDto.getTeamName()).isEqualTo("팀A");
    }

    @DisplayName("사용자 이름으로 인증서를 검색할 때 해당 이름의 인증서만 받는다")
    @Test
    void searchAdminCertificatesWithNameSearch() {
        //given
        int page = 0;
        int pageSize = 2;
        String orderType = "createdAt";
        String direction = "desc";

        // 테스트 데이터 생성
        Member leader1 = createAndSaveLeader("leader1", "김테스트");
        Member leader2 = createAndSaveLeader("leader2", "박테스트");
        Contest contest = createAndSaveContest();
        Team team1 = createAndSaveTeam(leader1, contest, "팀A","001");
        Team team2 = createAndSaveTeam(leader2, contest, "팀B","002");

        // 인증서 생성
        createAndSaveCertificate(leader1, team1, contest, CertificateType.PRELIMINARY);
        createAndSaveCertificate(leader2, team2, contest, CertificateType.FINAL);

        entityManager.flush();
        entityManager.clear();

        // 조건 설정 (이름으로 검색)
        AdminSearchCertificateCond cond = AdminSearchCertificateCond.builder()
                .page(page)
                .pageSize(pageSize)
                .orderType(orderType)
                .direction(direction)
                .searchType("name")
                .keyword("김테스트")
                .build();

        //when
        AdminSearchCertificateResponse response = certificateRepository.SearchAdminCertificate(cond);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getCertificateDtoList()).hasSize(1);
        assertThat(response.getTotalPage()).isEqualTo(1);

        AdminSearchCertificateDto certificateDto = response.getCertificateDtoList().get(0);
        assertThat(certificateDto.getTeamName()).isEqualTo("팀A");
    }

    @DisplayName("검색 타입을 지정하지 않으면 모든 인증서를 조회한다")
    @Test
    void searchAdminCertificatesWithoutSearchType() {
        //given
        int page = 0;
        int pageSize = 10;
        String orderType = "createdAt";
        String direction = "desc";

        Member leader1 = createAndSaveLeader("leader1", "김테스트");
        Member leader2 = createAndSaveLeader("leader2", "박테스트");
        Contest contest = createAndSaveContest();
        Team team1 = createAndSaveTeam(leader1, contest, "팀A","001");
        Team team2 = createAndSaveTeam(leader2, contest, "팀B","002");

        //인증서 생성
        createAndSaveCertificate(leader1, team1, contest, CertificateType.PRELIMINARY);
        createAndSaveCertificate(leader2, team2, contest, CertificateType.FINAL);

        entityManager.flush();
        entityManager.clear();

        //조건 설정 (검색 타입 없음)
        AdminSearchCertificateCond cond = AdminSearchCertificateCond.builder()
                .page(page)
                .pageSize(pageSize)
                .orderType(orderType)
                .direction(direction)
                .searchType("")
                .keyword("")
                .build();

        //when
        AdminSearchCertificateResponse response = certificateRepository.SearchAdminCertificate(cond);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getCertificateDtoList()).hasSize(2);
        assertThat(response.getTotalPage()).isEqualTo(1);
    }

    @DisplayName("지원하지 않는 검색 타입으로 검색할 때 모든 인증서를 조회한다")
    @Test
    void searchAdminCertificatesWithUnsupportedSearchType() {
        //given
        int page = 0;
        int pageSize = 10;
        String orderType = "createdAt";
        String direction = "desc";

        Member leader1 = createAndSaveLeader("leader1", "김테스트");
        Member leader2 = createAndSaveLeader("leader2", "박테스트");
        Contest contest = createAndSaveContest();
        Team team1 = createAndSaveTeam(leader1, contest, "팀A","001");
        Team team2 = createAndSaveTeam(leader2, contest, "팀B","002");

        // 인증서 생성
        createAndSaveCertificate(leader1, team1, contest, CertificateType.PRELIMINARY);
        createAndSaveCertificate(leader2, team2, contest, CertificateType.FINAL);

        entityManager.flush();
        entityManager.clear();

        // 조건 설정 (지원하지 않는 검색 타입)
        AdminSearchCertificateCond cond = AdminSearchCertificateCond.builder()
                .page(page)
                .pageSize(pageSize)
                .orderType(orderType)
                .direction(direction)
                .searchType("unsupportedType")
                .keyword("테스트")
                .build();

        //when
        AdminSearchCertificateResponse response = certificateRepository.SearchAdminCertificate(cond);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getCertificateDtoList()).hasSize(2);
        assertThat(response.getTotalPage()).isEqualTo(1);
    }

    @DisplayName("검색 키워드가 없을 때 모든 인증서를 조회한다")
    @Test
    void searchAdminCertificatesWithoutKeyword() {
        //given
        int page = 0;
        int pageSize = 10;
        String orderType = "createdAt";
        String direction = "desc";

        //테스트 데이터 생성
        Member leader1 = createAndSaveLeader("leader1", "김테스트");
        Member leader2 = createAndSaveLeader("leader2", "박테스트");
        Contest contest = createAndSaveContest();
        Team team1 = createAndSaveTeam(leader1, contest, "팀A","001");
        Team team2 = createAndSaveTeam(leader2, contest, "팀B","002");

        //인증서 생성
        createAndSaveCertificate(leader1, team1, contest, CertificateType.PRELIMINARY);
        createAndSaveCertificate(leader2, team2, contest, CertificateType.FINAL);

        entityManager.flush();
        entityManager.clear();

        //조건 설정 (키워드 없음)
        AdminSearchCertificateCond cond = AdminSearchCertificateCond.builder()
                .page(page)
                .pageSize(pageSize)
                .orderType(orderType)
                .direction(direction)
                .searchType("loginId")
                .keyword("")
                .build();

        //when
        AdminSearchCertificateResponse response = certificateRepository.SearchAdminCertificate(cond);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getCertificateDtoList()).hasSize(2);
        assertThat(response.getTotalPage()).isEqualTo(1);
    }

    private Member createAndSaveLeader(String loginId, String name) {
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        String phoneNumber = "010" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);

        Member leader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email(loginId + "email@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber(phoneNumber)
                .name(name)
                .organization(school)
                .build();
        return memberRepository.save(leader);
    }

    private Team createAndSaveTeam(Member leader, Contest contest, String teamName,String teamNumber) {
        Team team = Team.builder()
                .name(teamName)
                .winner(false)
                .leader(leader)
                .teamNumber(teamNumber)
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
                .phoneNumber("01012349876")
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