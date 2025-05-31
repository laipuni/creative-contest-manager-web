package com.example.cpsplatform.contest.admin.service;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.CertificateRepository;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.admin.controller.response.DeletedContestListResponse;
import com.example.cpsplatform.contest.admin.request.DeleteContestRequest;
import com.example.cpsplatform.contest.admin.request.UpdateContestRequest;
import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestDeleteDto;
import com.example.cpsplatform.contest.admin.service.dto.ContestUpdateDto;
import com.example.cpsplatform.contest.admin.service.dto.WinnerTeamsDto;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.DuplicateDataException;
import com.example.cpsplatform.finalcontest.FinalContest;
import com.example.cpsplatform.finalcontest.repository.FinalContestRepository;
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
import com.example.cpsplatform.team.domain.Division;
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.cpsplatform.certificate.domain.CertificateType.FINAL;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class ContestAdminServiceTest {

    @Autowired
    ContestAdminService contestAdminService;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberTeamRepository memberTeamRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    FinalContestRepository finalContestRepository;

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

    @DisplayName("예선 대회의 정보와 본선대회의 정보를 받아서 예선,본선 대회를 생성 및 저장한다.")
    @Test
    void createContestWithFinalContest(){
        //given
        String title = "title";
        int season = 1;
        String description ="대회 설명";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        String finalContestTitle = "본선 대회";
        String finalContestLocation = "대한민국";
        LocalDateTime finalContestStartTime = now.plusDays(5);
        LocalDateTime finalContestEndTime = now.plusDays(5).plusHours(2);

        ContestCreateDto contestCreateDto = new ContestCreateDto(title,season,description,
                registrationStartAt,registrationEndAt,contestStartAt,contestEndAt,
                finalContestTitle,finalContestLocation,finalContestStartTime,finalContestEndTime
                );
        //when
        contestAdminService.createContest(contestCreateDto);
        List<Contest> result = contestRepository.findAll();
        List<FinalContest> finalContests = finalContestRepository.findAll();
        //then
        assertThat(result.get(0))
                .extracting("title", "season", "description", "registrationStartAt", "registrationEndAt",
                        "startTime","endTime")
                .containsExactly(title,season,description,registrationStartAt,
                        registrationEndAt,contestStartAt,contestEndAt);
        assertThat(finalContests.get(0))
                .extracting("title", "location", "startTime", "endTime")
                .containsExactly(
                        finalContestTitle,
                        finalContestLocation,
                        finalContestStartTime,
                        finalContestEndTime
                );
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

    @DisplayName("예선 대회와 본선 대회의 정보를 수정한다.")
    @Test
    void updateContestWithModifyingFinalContest(){
        //given
        LocalDateTime now = LocalDateTime.now();
        //대회 생성
        FinalContest finalContest = FinalContest.builder()
                .title("테스트 본선 대회")
                .location("대한민국")
                .startTime(now().plusDays(10))
                .endTime(now().plusDays(10).plusHours(1))
                .build();

        Contest contest = Contest.builder()
                .title("16회 창의력 경진 대회 본선")
                .description("테스트 대회 설명")
                .season(16)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().plusHours(1))
                .startTime(now())
                .endTime(now().plusHours(2))
                .finalContest(finalContest)
                .build();

        contestRepository.save(contest);

        //수정할 예선 대회 정보
        String updatedTitle = "updatedTitle";
        int updatedSeason = 2;
        String updatedDescription ="수정된 대회 설명";
        LocalDateTime updatedRegistrationStartAt = now.plusDays(2);
        LocalDateTime updatedRegistrationEndAt= now.plusDays(3);
        LocalDateTime updatedContestStartAt = now.plusDays(4);
        LocalDateTime updatedContestEndAt = now.plusDays(5);

        //수정할 본선 대회 정보
        String finalContestTitle = "수정된 본선 대회 제목";
        String finalContestLocation = "수정된 장소";
        LocalDateTime finalContestStartTime = now.plusDays(7);
        LocalDateTime finalContestEndTime = now.plusDays(7).plusHours(2);

        ContestUpdateDto updateDto = new ContestUpdateDto(contest.getId(),updatedTitle,updatedSeason,updatedDescription,
                updatedRegistrationStartAt,updatedRegistrationEndAt,updatedContestStartAt,updatedContestEndAt,
                finalContestTitle,finalContestLocation,finalContestStartTime,finalContestEndTime);
        //when
        contestAdminService.updateContest(updateDto);
        List<Contest> result = contestRepository.findAll();
        List<FinalContest> finalContests = finalContestRepository.findAll();

        //then
        assertThat(result.get(0))
                .extracting("title", "season", "description", "registrationStartAt", "registrationEndAt",
                        "startTime","endTime")
                .containsExactly(updatedTitle,updatedSeason,updatedDescription,
                        updatedRegistrationStartAt,updatedRegistrationEndAt,updatedContestStartAt,updatedContestEndAt);
        assertThat(finalContests.get(0))
                .extracting("title", "location", "startTime", "endTime")
                .containsExactly(
                        finalContestTitle,
                        finalContestLocation,
                        finalContestStartTime,
                        finalContestEndTime
                );
    }

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
        FinalContest finalContest = FinalContest.builder()
                .title("테스트 본선 대회")
                .location("대한민국")
                .startTime(now().plusDays(10))
                .endTime(now().plusDays(10).plusHours(1))
                .build();
        Contest contest = Contest.builder()
                .title("title")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .finalContest(finalContest)
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

    @DisplayName("대회를 임시 삭제한다.")
    @Test
    void deleteContest(){
        //given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        //contest 생성
        FinalContest finalContest = FinalContest.builder()
                .title("테스트 본선 대회")
                .location("대한민국")
                .startTime(now().plusDays(10))
                .endTime(now().plusDays(10).plusHours(1))
                .build();
        Contest contest = Contest.builder()
                .title("title")
                .description("대회 설명")
                .season(1)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .finalContest(finalContest)
                .build();

        contestRepository.save(contest);

        ContestDeleteDto request = new ContestDeleteDto(contest.getId());

        //when
        contestAdminService.deleteContest(request);
        List<Contest> result = contestRepository.findAll();
        //then
        assertThat(result).hasSize(0);
    }

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
                .email(loginId + "@email.com")
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
                .status(SubmitStatus.FINAL)
                .division(Division.ELEMENTARY)
                .section(Section.ELEMENTARY_MIDDLE)
                .build();
        teamRepository.save(team);

        Team team1 = Team.builder()
                .name("테스트팀1")
                .winner(false)
                .leader(leader1)
                .contest(contest)
                .teamNumber("004")
                .status(SubmitStatus.FINAL)
                .section(Section.ELEMENTARY_MIDDLE)
                .division(Division.ELEMENTARY)
                .build();
        teamRepository.save(team1);

        List<Long> winnerTeamIds = List.of(team.getId(), team1.getId());
        WinnerTeamsDto dto = new WinnerTeamsDto(winnerTeamIds);

        // when
        contestAdminService.toggleWinnerTeams(contest.getId(), dto);

        // then
        List<Team> result = teamRepository.findAllById(winnerTeamIds);
        assertThat(result).extracting(Team::getWinner).containsOnly(true);
    }
  
    @DisplayName("임시 삭제된 대회를 복구한다.")
    @Test
    void recoverContest() {
        //given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt = now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);

        //contest 생성
        Contest contest = Contest.builder()
                .title("title")
                .description("대회 설명")
                .season(16)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();

        //저장 후 삭제
        contestRepository.save(contest);
        entityManager.flush();
        entityManager.clear();

        contestRepository.deleteById(contest.getId());
        entityManager.flush();
        entityManager.clear();

        //when
        contestAdminService.recoverContest(contest.getId());

        //then
        List<Contest> result = contestRepository.findAll();
        assertThat(result).hasSize(1); // 복구된 대회가 존재해야 함
        assertThat(result.get(0).getId()).isEqualTo(contest.getId());
        assertThat(result.get(0).getTitle()).isEqualTo("title");
    }

    @DisplayName("임시 삭제된 대회들을 조회한다.")
    @Test
    void findDeletedContest(){
        //given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        //contest 생성
        Contest contest1 = Contest.builder()
                .title("title")
                .description("대회 설명")
                .season(16)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();
        Contest contest2 = Contest.builder()
                .title("title")
                .description("대회 설명")
                .season(17)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();
        //대회들을 미리 저장
        contestRepository.saveAll(List.of(contest1,contest2));
        entityManager.flush();
        entityManager.clear();

        //미리 저장한 대회를 전부 소프트 삭제
        contestRepository.deleteAllById(List.of(contest1.getId(),contest2.getId()));
        entityManager.flush();
        entityManager.clear();

        //when
        DeletedContestListResponse result = contestAdminService.findDeletedContest();

        //then
        assertThat(result.getDeletedContestList()).hasSize(2)
                .extracting("contestId", "title", "season")
                .containsExactly(
                        tuple(contest1.getId(), contest1.getTitle(), contest1.getSeason()),
                        tuple(contest2.getId(), contest2.getTitle(), contest2.getSeason())
                );
    }

    @DisplayName("팀들의 예선 합격 여부를 바꿀 때, 합격 처리할 팀만 있을 경우 합격과 확인증만 생성한다.")
    @Test
    void toggleWinnerTeamsWithOnlyWinnerTeams(){
        //given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(1)
                .registrationStartAt(now.minusDays(5)) //테스트 시점 5일 전
                .registrationEndAt(now.plusDays(2)) //테스트 시점 2일 후 마감
                .startTime(now.plusDays(3)) //테스트 시점 3일 후 시작
                .endTime(now.plusHours(4)) //테스트 시점 4일 후
                .build();
        contestRepository.save(contest);

        //팀의 팀장 유저 세팅
        Member member1 = createAndSaveMember("member1");
        Member member2 = createAndSaveMember("member2");
        Member member3 = createAndSaveMember("member3");
        Member member4 = createAndSaveMember("member4");

        //팀 세팅
        Team team1 = createAndSaveTeam(member1, contest, "001", false);
        Team team2 = createAndSaveTeam(member2, contest, "002", false);
        Team team3 = createAndSaveTeam(member3, contest, "003", false);
        Team team4 = createAndSaveTeam(member4, contest, "004", false);

        WinnerTeamsDto winnerTeamsDto = new WinnerTeamsDto(List.of(
                team1.getId(),team2.getId(),team3.getId(),team4.getId()
        ));

        //when
        contestAdminService.toggleWinnerTeams(contest.getId(),winnerTeamsDto);

        //then
        List<Team> teams = teamRepository.findAll();
        List<Certificate> certificates = certificateRepository.findAll();

        assertThat(teams).hasSize(4)
                .extracting("winner")
                .containsExactly(true,true,true,true);
        assertThat(certificates).hasSize(4)
                .extracting("certificateType")
                .containsExactly(FINAL,FINAL,FINAL,FINAL);
    }

    @DisplayName("팀들의 예선 합격 여부를 바꿀 때, 합격,불합격 처리할 팀이 있을 경우 합격과 확인증만 생성한다.")
    @Test
    void toggleWinnerTeamsWithWinnerTeamAndLoserTeam(){
        //given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(1)
                .registrationStartAt(now.minusDays(5)) //테스트 시점 5일 전
                .registrationEndAt(now.plusDays(2)) //테스트 시점 2일 후 마감
                .startTime(now.plusDays(3)) //테스트 시점 3일 후 시작
                .endTime(now.plusHours(4)) //테스트 시점 4일 후
                .build();
        contestRepository.save(contest);

        //팀의 팀장 유저 세팅
        Member member1 = createAndSaveMember("member1");
        Member member2 = createAndSaveMember("member2");
        Member member3 = createAndSaveMember("member3");
        Member member4 = createAndSaveMember("member4");

        //팀 세팅
        Team team1 = createAndSaveTeam(member1, contest, "001", false); // 불합격 -> 합격
        Team team2 = createAndSaveTeam(member2, contest, "002", false); // 불합격 -> 합격
        Team team3 = createAndSaveTeam(member3, contest, "003", true);  // 합격 -> 불합격
        Team team4 = createAndSaveTeam(member4, contest, "004", true);  // 합격 -> 불합격

        //본선 확인증 세팅
        Certificate certificate1 = createAndSaveCertificate(member3, contest, team3);
        Certificate certificate2 = createAndSaveCertificate(member4, contest, team4);

        //영속성 컨텍스트 비우기
        entityManager.flush();
        entityManager.clear();

        WinnerTeamsDto winnerTeamsDto = new WinnerTeamsDto(List.of(
                team1.getId(),team2.getId(),team3.getId(),team4.getId()
        ));

        //when
        contestAdminService.toggleWinnerTeams(contest.getId(),winnerTeamsDto);

        //then
        List<Team> teams = teamRepository.findAll();
        List<Certificate> certificates = certificateRepository.findAll();

        assertThat(teams).hasSize(4)
                .extracting("winner")
                .containsExactly(true,true,false,false);
        assertThat(certificates).hasSize(2)
                .extracting("certificateType")
                .containsExactly(FINAL,FINAL);
    }

    @DisplayName("팀들의 예선 합격 여부를 바꿀 때, 불합격 처리할 팀만 있을 경우 합격과 확인증만 생성한다.")
    @Test
    void toggleWinnerTeamsWithOnlyLoserTeam(){
        //given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .season(1)
                .registrationStartAt(now.minusDays(5)) //테스트 시점 5일 전
                .registrationEndAt(now.plusDays(2)) //테스트 시점 2일 후 마감
                .startTime(now.plusDays(3)) //테스트 시점 3일 후 시작
                .endTime(now.plusHours(4)) //테스트 시점 4일 후
                .build();
        contestRepository.save(contest);

        //팀의 팀장 유저 세팅
        Member member1 = createAndSaveMember("member1");
        Member member2 = createAndSaveMember("member2");
        Member member3 = createAndSaveMember("member3");
        Member member4 = createAndSaveMember("member4");

        //팀 세팅
        Team team1 = createAndSaveTeam(member1, contest, "001", true); // 합격 -> 불합격
        Team team2 = createAndSaveTeam(member2, contest, "002", true); // 합격 -> 불합격
        Team team3 = createAndSaveTeam(member3, contest, "003", true);  // 합격 -> 불합격
        Team team4 = createAndSaveTeam(member4, contest, "004", true);  // 합격 -> 불합격

        //본선 확인증 세팅
        Certificate certificate1 = createAndSaveCertificate(member1, contest, team1);
        Certificate certificate2 = createAndSaveCertificate(member2, contest, team2);
        Certificate certificate3 = createAndSaveCertificate(member3, contest, team3);
        Certificate certificate4 = createAndSaveCertificate(member4, contest, team4);

        //영속성 컨텍스트 비우기
        entityManager.flush();
        entityManager.clear();

        WinnerTeamsDto winnerTeamsDto = new WinnerTeamsDto(List.of(
                team1.getId(),team2.getId(),team3.getId(),team4.getId()
        ));

        //when
        contestAdminService.toggleWinnerTeams(contest.getId(),winnerTeamsDto);

        //then
        List<Team> teams = teamRepository.findAll();
        List<Certificate> certificates = certificateRepository.findAll();

        assertThat(teams).hasSize(4)
                .extracting("winner")
                .containsExactly(false,false,false,false);
        assertThat(certificates).isEmpty();
    }

    private Member createAndSaveMember(String loginId) {
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        String phoneNumber = "010" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);
        Member member = Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email(loginId + "@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber(phoneNumber)
                .name("리더")
                .organization(school)
                .build();
        return memberRepository.save(member);
    }

    private Team createAndSaveTeam(Member leader, Contest contest, String teamNumber, boolean isWinner) {
        Team team = Team.builder()
                .name("팀" + teamNumber)
                .winner(isWinner)
                .leader(leader)
                .teamNumber(teamNumber)
                .status(SubmitStatus.FINAL)
                .division(Division.ELEMENTARY)
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

    private Certificate createAndSaveCertificate(Member member, Contest contest, Team team){
        Certificate certificate = Certificate.builder()
                .title("본선 진출 확인증")
                .certificateType(FINAL)
                .serialNumber(UUID.randomUUID().toString())
                .contest(contest)
                .team(team)
                .member(member)
                .build();
        return certificateRepository.save(certificate);
    }

}