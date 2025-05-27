package com.example.cpsplatform.team.service;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.CertificateRepository;
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
import com.example.cpsplatform.team.domain.Division;
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.team.service.dto.MyTeamInfoByContestDto;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import com.example.cpsplatform.team.service.dto.TeamUpdateDto;
import com.example.cpsplatform.teamnumber.domain.TeamNumber;
import com.example.cpsplatform.teamnumber.repository.TeamNumberRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@SpringBootTest
class TeamServiceIntegrationTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamNumberRepository teamNumberRepository;

    @Autowired
    private MemberTeamRepository memberTeamRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CryptoService cryptoService;

    @Autowired
    EntityManager entityManager;

    @DisplayName("팀장과 멤버가 주어질 경우 팀 생성이 정상적으로 동작한다.")
    @Test
    void createTeam() {
        // given
        String leaderId = "leaderId";
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
        memberRepository.saveAll(List.of(member1,member2,leader));

        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(2025)
                .registrationStartAt(LocalDateTime.now())
                .registrationEndAt(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
        contestRepository.save(contest);

        TeamCreateDto dto = new TeamCreateDto("팀입니다", contest.getId(), List.of("one", "two"));

        TeamNumber teamNumber = TeamNumber.builder().contest(contest).lastTeamNumber(2).build();
        teamNumberRepository.save(teamNumber);

        // when
        Long createdTeamId = teamService.createTeam(leaderId, dto);

        // then
        Team savedTeam = teamRepository.findById(createdTeamId).orElseThrow();

        //팀 번호가 003인지 확인
        assertEquals("003", savedTeam.getTeamNumber());
        //팀 섹션 확인
        assertEquals(Section.HIGH_NORMAL, savedTeam.getSection());
        //팀 생성 반환 확인
        assertEquals("팀입니다", savedTeam.getName());
        assertEquals(contest, savedTeam.getContest());
        assertEquals(leader, savedTeam.getLeader());
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

        Team team = Team.builder()
                .name("이팀")
                .winner(false)
                .leader(member)
                .teamNumber("003")
                .status(SubmitStatus.NOT_SUBMITTED)
                .contest(contest)
                .division(Division.COLLEGE_GENERAL)
                .build();
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

        memberTeamRepository.saveAll(List.of(
                MemberTeam.of(member, team),
                MemberTeam.of(member2, team)
        ));

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

    @DisplayName("원래있던 팀원은 수정없이 팀의 이름만 수정한다.")
    @Test
    void updateTeam(){
        // given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("leader@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01011112222")
                .name("사람 이름")
                .organization(school)
                .build();

        String loginId1 = "member";
        Address address1 = new Address("street","city","zipCode","detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member1 = Member.builder()
                .loginId(loginId1)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("member1@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01033334444")
                .name("사람 이름2")
                .organization(school1)
                .build();
        String loginId2 = "member2";
        Address address2 = new Address("street","city","zipCode","detail");
        School school2 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member2 = Member.builder()
                .loginId(loginId2)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("member2@email.com")
                .address(address2)
                .gender(Gender.MAN)
                .phoneNumber("01055556666")
                .name("사람 이름2")
                .organization(school2)
                .build();
        memberRepository.saveAll(List.of(leader,member1,member2));

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
                .name("이팀")
                .winner(false)
                .leader(leader)
                .teamNumber("003")
                .contest(contest)
                .division(Division.COLLEGE_GENERAL)
                .status(SubmitStatus.NOT_SUBMITTED)
                .build();
        teamRepository.save(team);

        //미리 팀을 구성
        memberTeamRepository.saveAll(List.of(
                MemberTeam.of(member1, team), //팀원 1
                MemberTeam.of(member2, team), //팀원 2
                MemberTeam.of(leader, team) //리더
        )); //미리 넣고

        entityManager.flush();
        entityManager.clear();

        //팀원1,팀원2 그대로 이름만 변경
        TeamUpdateDto updateDto = new TeamUpdateDto("수정된 팀이름",List.of(member1.getLoginId(),member2.getLoginId()),contest.getId());

        //when
        teamService.updateTeam(team.getId(),updateDto,leader.getLoginId());

        //then
        List<MemberTeam> memberTeams = memberTeamRepository.findAll();
        assertThat(memberTeams).hasSize(3);
    }

    @DisplayName("원래 있던 팀원 중 한명을 팀에서 제외한다.")
    @Test
    void updateTeamWithChangeTeammate(){
        // given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("leader@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01011112222")
                .name("사람 이름")
                .organization(school)
                .build();

        String loginId1 = "member";
        Address address1 = new Address("street","city","zipCode","detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member1 = Member.builder()
                .loginId(loginId1)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("member1@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01033334444")
                .name("사람 이름2")
                .organization(school1)
                .build();
        String loginId2 = "member2";
        Address address2 = new Address("street","city","zipCode","detail");
        School school2 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member2 = Member.builder()
                .loginId(loginId2)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("member2@email.com")
                .address(address2)
                .gender(Gender.MAN)
                .phoneNumber("01055556666")
                .name("사람 이름2")
                .organization(school2)
                .build();
        memberRepository.saveAll(List.of(leader,member1,member2));

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
                .name("이팀")
                .winner(false)
                .leader(leader)
                .teamNumber("003")
                .contest(contest)
                .division(Division.COLLEGE_GENERAL)
                .status(SubmitStatus.NOT_SUBMITTED)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam1 = MemberTeam.of(member1, team);//팀원 1
        MemberTeam memberTeam2 = MemberTeam.of(member2, team);//팀원 2
        MemberTeam memberTeam3 = MemberTeam.of(leader, team);//리더

        memberTeamRepository.saveAll(List.of(memberTeam1, memberTeam2, memberTeam3)); //미리 넣고

        Certificate certificate1 = Certificate.builder()
                .member(member1)
                .title("16회 예선 참가 확인증")
                .team(team)
                .serialNumber(UUID.randomUUID().toString())
                .certificateType(CertificateType.PRELIMINARY)
                .build();

        Certificate certificate2 = Certificate.builder()
                .member(member2)
                .title("16회 예선 참가 확인증")
                .team(team)
                .serialNumber(UUID.randomUUID().toString())
                .certificateType(CertificateType.PRELIMINARY)
                .build();

        Certificate certificate3 = Certificate.builder()
                .member(leader)
                .title("16회 예선 참가 확인증")
                .team(team)
                .serialNumber(UUID.randomUUID().toString())
                .certificateType(CertificateType.PRELIMINARY)
                .build();

        certificateRepository.saveAll(List.of(certificate1,certificate2,certificate3));

        entityManager.flush();
        entityManager.clear();

        //팀원1은 그대로, 팀원 2 제외
        TeamUpdateDto updateDto = new TeamUpdateDto("수정된 팀이름",List.of(member1.getLoginId()),contest.getId());

        //when
        teamService.updateTeam(team.getId(),updateDto,leader.getLoginId());

        //then
        List<MemberTeam> memberTeams = memberTeamRepository.findAll();
        List<Certificate> certificates = certificateRepository.findAll();
        assertThat(memberTeams).hasSize(2);
        assertThat(certificates).hasSize(2);
    }

    @DisplayName("원래 있던 팀원 중 한명을 더 추가한다.")
    @Test
    void updateTeamWithAddTeammate(){
        // given
        String loginId = "loginId";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member leader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("leader@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01011112222")
                .name("사람 이름")
                .organization(school)
                .build();

        String loginId1 = "member";
        Address address1 = new Address("street","city","zipCode","detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member1 = Member.builder()
                .loginId(loginId1)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("member1@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01033334444")
                .name("사람 이름2")
                .organization(school1)
                .build();
        String loginId2 = "member2";
        Address address2 = new Address("street","city","zipCode","detail");
        School school2 = new School("xx대학교", StudentType.COLLEGE,4);
        Member member2 = Member.builder()
                .loginId(loginId2)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("member2@email.com")
                .address(address2)
                .gender(Gender.MAN)
                .phoneNumber("01055556666")
                .name("사람 이름2")
                .organization(school2)
                .build();
        memberRepository.saveAll(List.of(leader,member1,member2));

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
                .name("이팀")
                .winner(false)
                .leader(leader)
                .teamNumber("003")
                .contest(contest)
                .division(Division.COLLEGE_GENERAL)
                .status(SubmitStatus.NOT_SUBMITTED)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam1 = MemberTeam.of(member1, team);//팀원 1
        MemberTeam memberTeam3 = MemberTeam.of(leader, team);//리더

        memberTeamRepository.saveAll(List.of(memberTeam1, memberTeam3)); //미리 넣고

        Certificate certificate1 = Certificate.builder()
                .member(member1)
                .title("16회 예선 참가 확인증")
                .team(team)
                .serialNumber(UUID.randomUUID().toString())
                .certificateType(CertificateType.PRELIMINARY)
                .build();

        Certificate certificate2 = Certificate.builder()
                .member(leader)
                .title("16회 예선 참가 확인증")
                .team(team)
                .serialNumber(UUID.randomUUID().toString())
                .certificateType(CertificateType.PRELIMINARY)
                .build();

        certificateRepository.saveAll(List.of(certificate1,certificate2));

        entityManager.flush();
        entityManager.clear();

        //팀원1은 그대로, 팀원 2 추가
        TeamUpdateDto updateDto = new TeamUpdateDto("수정된 팀이름",List.of(member1.getLoginId(),member2.getLoginId()),contest.getId());

        //when
        teamService.updateTeam(team.getId(),updateDto,leader.getLoginId());

        //then
        List<MemberTeam> memberTeams = memberTeamRepository.findAll();
        List<Certificate> certificates = certificateRepository.findAll();
        assertThat(memberTeams).hasSize(3);
        assertThat(certificates).hasSize(3);
    }

    @DisplayName("팀을 생성할 때, 해당팀에 맞지 않는 부문에 팀원이 있을 경우 예외가 발생한다.")
    @Test
    void createTeamWithMismatchElementaryTeammate(){
        //given
        String loginId = "elementary";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx초등학교", StudentType.ELEMENTARY,4);
        Member elementaryLeader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("elementaryLeader@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01099999999")
                .name("사람 이름")
                .organization(school)
                .build();

        String loginId1 = "college";
        Address address1 = new Address("street","city","zipCode","detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE,4);
        Member notElementaryMember = Member.builder()
                .loginId(loginId1)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("college@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01033334444")
                .name("사람 이름2")
                .organization(school1)
                .build();

        memberRepository.saveAll(List.of(elementaryLeader,notElementaryMember));

        Contest contest = Contest.builder()
                .title("테스트대회")
                .season(2025)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        TeamNumber teamNumber = TeamNumber.builder()
                .lastTeamNumber(0)
                .contest(contest)
                .build();

        teamNumberRepository.save(teamNumber);

        TeamCreateDto dto = new TeamCreateDto("초등부 팀", contest.getId(), List.of(notElementaryMember.getLoginId()));


        //when
        //then
        assertThatThrownBy(()->teamService.createTeam(elementaryLeader.getLoginId(), dto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageMatching(String.format(
                        "%s님은 %s에 들어갈 수 없습니다.",
                        notElementaryMember.getLoginId(), Division.ELEMENTARY.getDescription()
                ));
    }


    @DisplayName("팀을 생성할 때, 해당팀에 맞지 않는 부문에 팀원이 있을 경우 예외가 발생한다.")
    @Test
    void createTeamWithMismatchTeammate(){
        //given
        String loginId = "middle";
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx중학교", StudentType.MIDDLE,4);
        Member middleLeader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("middle@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01099999999")
                .name("사람 이름")
                .organization(school)
                .build();

        String loginId1 = "college";
        Address address1 = new Address("street","city","zipCode","detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE,4);
        Member notMiddleMember = Member.builder()
                .loginId(loginId1)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("college@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01033334444")
                .name("사람 이름2")
                .organization(school1)
                .build();

        memberRepository.saveAll(List.of(middleLeader,notMiddleMember));

        Contest contest = Contest.builder()
                .title("테스트대회")
                .season(2025)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        TeamNumber teamNumber = TeamNumber.builder()
                .lastTeamNumber(0)
                .contest(contest)
                .build();

        teamNumberRepository.save(teamNumber);

        TeamCreateDto dto = new TeamCreateDto("중등부 팀", contest.getId(), List.of(notMiddleMember.getLoginId()));

        //when
        //then
        assertThatThrownBy(()->teamService.createTeam(middleLeader.getLoginId(), dto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageMatching(String.format(
                        "%s님은 %s에 들어갈 수 없습니다.",
                        notMiddleMember.getLoginId(), Division.MIDDLE.getDescription()
                ));
    }

    @DisplayName("팀을 생성할 때, 고등부 팀에 대학생이 팀원으로 있으면 예외가 발생한다.")
    @Test
    void createTeamWithMismatchHighSchoolTeammate() {
        //given
        String loginId = "high";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx고등학교", StudentType.HIGH, 2);
        Member highLeader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("high@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01088889999")
                .name("고등부 리더")
                .organization(school)
                .build();

        String loginId1 = "college";
        Address address1 = new Address("street", "city", "zipCode", "detail");
        School school1 = new School("xx대학교", StudentType.COLLEGE, 1);
        Member collegeMember = Member.builder()
                .loginId(loginId1)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("college@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01011112222")
                .name("대학생")
                .organization(school1)
                .build();

        memberRepository.saveAll(List.of(highLeader, collegeMember));

        Contest contest = Contest.builder()
                .title("테스트대회")
                .season(2025)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        TeamNumber teamNumber = TeamNumber.builder()
                .lastTeamNumber(0)
                .contest(contest)
                .build();

        teamNumberRepository.save(teamNumber);

        TeamCreateDto dto = new TeamCreateDto("고등부 팀", contest.getId(), List.of(collegeMember.getLoginId()));

        //when
        //then
        assertThatThrownBy(() -> teamService.createTeam(highLeader.getLoginId(), dto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageMatching(String.format(
                        "%s님은 %s에 들어갈 수 없습니다.",
                        collegeMember.getLoginId(), Division.HIGH.getDescription()
                ));
    }

    @DisplayName("팀을 생성할 때, 대학/일반부 팀에 초등학생이 팀원으로 있으면 예외가 발생한다.")
    @Test
    void createTeamWithMismatchCommonDivisionTeammate() {
        // given
        String loginId = "college";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 3);
        Member collegeLeader = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("collegeLeader@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01012345678")
                .name("대학생 리더")
                .organization(school)
                .build();

        String loginId1 = "elementary";
        Address address1 = new Address("street", "city", "zipCode", "detail");
        School school1 = new School("xx초등학교", StudentType.ELEMENTARY, 5);
        Member elementaryMember = Member.builder()
                .loginId(loginId1)
                .password(passwordEncoder.encode("1235"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("elementary@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01055556666")
                .name("초등학생")
                .organization(school1)
                .build();

        memberRepository.saveAll(List.of(collegeLeader, elementaryMember));

        Contest contest = Contest.builder()
                .title("테스트대회")
                .season(2025)
                .registrationStartAt(LocalDate.now().atStartOfDay())
                .registrationEndAt(LocalDate.now().plusDays(5).atStartOfDay())
                .startTime(LocalDate.now().atStartOfDay())
                .endTime(LocalDate.now().plusDays(7).atStartOfDay())
                .build();
        contestRepository.save(contest);

        TeamNumber teamNumber = TeamNumber.builder()
                .lastTeamNumber(0)
                .contest(contest)
                .build();

        teamNumberRepository.save(teamNumber);

        TeamCreateDto dto = new TeamCreateDto("대학/일반부 팀", contest.getId(), List.of(elementaryMember.getLoginId()));

        //when
        //then
        assertThatThrownBy(() -> teamService.createTeam(collegeLeader.getLoginId(), dto))
                .isInstanceOf(ContestJoinException.class)
                .hasMessageMatching(String.format(
                        "%s님은 %s에 들어갈 수 없습니다.",
                        elementaryMember.getLoginId(), Division.COLLEGE_GENERAL.getDescription()
                ));
    }

}