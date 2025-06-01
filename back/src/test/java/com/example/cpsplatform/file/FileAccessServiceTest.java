package com.example.cpsplatform.file;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.FileDownloadAuthException;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.notice.domain.Notice;
import com.example.cpsplatform.notice.repository.NoticeRepository;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import com.example.cpsplatform.team.domain.Division;
import com.example.cpsplatform.team.domain.SubmitStatus;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FileAccessServiceTest {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TeamSolveRepository teamSolveRepository;

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    FileAccessService fileAccessService;

    @Autowired
    MemberTeamRepository memberTeamRepository;

    @DisplayName("해당 파일이 팀이 제출한 답안지인지 확인한다.")
    @Test
    void validateMemberFileAccess(){
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("teamsolve@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01045686542")
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

        Team team = Team.builder()
                .name("팀 이름")
                .winner(false)
                .teamNumber("001")
                .leader(member)
                .contest(contest)
                .division(Division.COLLEGE_GENERAL)
                .status(SubmitStatus.NOT_SUBMITTED)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam = MemberTeam.builder()
                .member(member)
                .team(team)
                .build();
        memberTeamRepository.save(memberTeam);

        Problem problem = Problem.builder()
                .title("첫번째 문제 제목")
                .contest(contest)
                .section(Section.HIGH_NORMAL)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("첫번째 문제 설명")
                .build();

        problemRepository.save(problem);

        TeamSolve teamSolve = TeamSolve.builder()
                .team(team)
                .problem(problem)
                .teamSolveType(TeamSolveType.TEMP)
                .build();
        teamSolveRepository.save(teamSolve);

        //teamsolve를 참조하는 file
        File file = File.builder()
                .teamSolve(teamSolve)
                .name("답안지1_1.pdf")
                .originalName("답안지1_1.pdf")
                .fileType(FileType.TEAM_SOLUTION)
                .mimeType(FileExtension.PDF.getMimeType())
                .extension(FileExtension.PDF)
                .size(100L)
                .path("/contest/16회/고등-일반/1번")
                .build();

        fileRepository.save(file);
        //when
        boolean result = fileRepository.existsAnswerFileForTeam(team.getId(), file.getId(), FileType.TEAM_SOLUTION);
        //then
        assertThatNoException().isThrownBy(() ->
                fileAccessService.validateMemberFileAccess(team.getId(), file.getId(), member.getLoginId())
        );
    }

    @DisplayName("해당 팀의 팀원이 아닌 경우 예외가 발생한다.")
    @Test
    void validateMemberFileAccessWithNotTeammate(){
        //given
        //when
        //then
        assertThatThrownBy(() -> fileAccessService.validateMemberFileAccess(999L, 999L, "invalidLoginId"))
                .isInstanceOf(FileDownloadAuthException.class)
                .hasMessageMatching("해당 파일을 다운로드 받을 권한이 없습니다.");
    }

    @DisplayName("다운로드할 파일이 팀의 답안지가 아닌 경우 예외가 발생한다.")
    @Test
    void validateMemberFileAccessWithNotAnswerFile(){
        //given
        Address address = new Address("street","city","zipCode","detail");
        School school = new School("xx대학교", StudentType.COLLEGE,4);
        Member member = Member.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .birth(LocalDate.now())
                .email("teamsolve@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber("01045686542")
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

        Team team = Team.builder()
                .name("팀 이름")
                .winner(false)
                .teamNumber("001")
                .leader(member)
                .contest(contest)
                .division(Division.COLLEGE_GENERAL)
                .status(SubmitStatus.NOT_SUBMITTED)
                .build();
        teamRepository.save(team);

        MemberTeam memberTeam = MemberTeam.builder()
                .member(member)
                .team(team)
                .build();
        memberTeamRepository.save(memberTeam);

        //유효하지 않은 파일의 id
        Long invalidFileId = 999L;

        //when
        //then
        assertThatThrownBy(() -> fileAccessService.validateMemberFileAccess(team.getId(), invalidFileId,member.getLoginId()))
                .isInstanceOf(FileDownloadAuthException.class)
                .hasMessageMatching("해당 파일을 다운로드 받을 수 없습니다.");
    }


    @DisplayName("해당 파일이 공지사항에 첨부된 파일인지 확인한다.(첨부된 파일이 아닐 경우)")
    @Test
    void validateNoticeFileAccessWithFail(){
        //given
        String loginId = "admin";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        String phoneNumber = "010" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);
        Member admin = Member.builder()
                .loginId(loginId)
                .password("1234")
                .role(Role.ADMIN)
                .birth(LocalDate.now())
                .email(loginId + "@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber(phoneNumber)
                .name("리더")
                .organization(school)
                .build();
        memberRepository.save(admin);

        Notice notice = Notice.builder()
                .viewCount(0L)
                .writer(admin)
                .title("공지사항")
                .content("공지사항 내용")
                .build();
        noticeRepository.save(notice);

        //when
        //then
        assertThatThrownBy(() -> fileAccessService.validateNoticeFileAccess(notice.getId(), 999L))
                .isInstanceOf(FileDownloadAuthException.class)
                .hasMessageMatching("해당 파일을 다운로드 받을 권한이 없습니다.");

    }

    @DisplayName("해당 파일이 공지사항에 첨부된 파일인지 확인한다.(첨부된 파일인 경우)")
    @Test
    void validateNoticeFileAccessWithSuccess(){
        //given
        String loginId = "admin";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        String phoneNumber = "010" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);
        Member admin = Member.builder()
                .loginId(loginId)
                .password("1234")
                .role(Role.ADMIN)
                .birth(LocalDate.now())
                .email(loginId + "@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber(phoneNumber)
                .name("리더")
                .organization(school)
                .build();
        memberRepository.save(admin);

        Notice notice = Notice.builder()
                .viewCount(0L)
                .writer(admin)
                .title("공지사항")
                .content("공지사항 내용")
                .build();
        noticeRepository.save(notice);

        File file = File.builder()
                .name("삭제할_파일1.pdf")
                .originalName("삭제할_파일1.pdf")
                .extension(FileExtension.PDF)
                .mimeType(FileExtension.PDF.getMimeType())
                .size(100L)
                .path("/notice/" + FileExtension.PDF.getExtension())
                .fileType(FileType.NOTICE)
                .notice(notice)
                .build();

        fileRepository.save(file);
        //when
        //then
        assertDoesNotThrow(() -> fileAccessService.validateNoticeFileAccess(notice.getId(), file.getId()));
    }
}