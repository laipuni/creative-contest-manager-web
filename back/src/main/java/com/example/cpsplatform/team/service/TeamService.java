package com.example.cpsplatform.team.service;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.repository.CertificateRepository;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.ContestJoinException;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.organization.Organization;
import com.example.cpsplatform.member.domain.organization.company.Company;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.team.domain.Division;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.policy.TeamJoinEligibilityPolicy;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.team.service.dto.MyTeamInfoByContestDto;
import com.example.cpsplatform.team.service.dto.MyTeamInfoDto;
import com.example.cpsplatform.team.service.dto.TeamCreateDto;
import com.example.cpsplatform.team.service.dto.TeamUpdateDto;
import com.example.cpsplatform.teamnumber.domain.TeamNumber;
import com.example.cpsplatform.teamnumber.repository.TeamNumberRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final ContestRepository contestRepository;
    private final TeamNumberRepository teamNumberRepository;
    private final TeamJoinEligibilityPolicy teamJoinEligibilityPolicy;
    private final CertificateRepository certificateRepository;

    @Transactional
    public Long createTeam(String leaderId, TeamCreateDto createDto){
        Member leader = memberRepository.findMemberByLoginId(leaderId)
                .orElseThrow(()->new IllegalArgumentException("해당 팀장은 존재하지 않습니다."));

        Contest contest = findContestById(createDto.getContestId());
        log.info("조회된 대회: {} (id: {})", contest.getTitle(), contest.getId());
        //접수하는 시점이 대회의 접수 기간인지 검증
        validateContestJoin(contest);

        TeamNumber teamNumber = teamNumberRepository.getLockedNumberForContest(createDto.getContestId())
                .orElseThrow(() -> new IllegalArgumentException("팀 접수 번호를 생성하는데, 문제가 발생했습니다."));

        String teamIdNumber = teamNumber.getNextTeamNumber();
        Team team = Team.of(createDto.getTeamName(), false, leader, contest, teamIdNumber);

        teamRepository.save(team);
        memberTeamRepository.save(MemberTeam.of(leader, team));

        //팀장의 예선 참가 확인증 만들기
        createAndSaveCertificate(team,contest,leader);

        validateTeamSize(createDto.getMemberIds());
        addMembersToTeam(createDto.getMemberIds(), team, contest);
        return team.getId();
    }

    private static void validateContestJoin(final Contest contest) {
        if(contest.isNotRegistering(LocalDateTime.now())){
            //대회의 접수기간이 아닌 경우
            throw new ContestJoinException(
                    String.format("%d회 대회는 현재 접수 기간이 아닙니다.", contest.getSeason())
            );
        }
    }

    @Transactional
    public void updateTeam(Long teamId, TeamUpdateDto updateDto, String loginId){
        Member leader = memberRepository.findMemberByLoginId(loginId)
                .orElseThrow(()->new IllegalArgumentException("해당 팀장은 존재하지 않습니다."));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()->new IllegalArgumentException("해당 팀은 존재하지 않습니다."));
        team.isNotTeamLeader(team, loginId);

        //팀 수정은 접수기간에만 가능하기에 수정 시간 검증
        Contest contest = findContestById(updateDto.getContestId());
        validateContestJoin(contest);

        team.updateTeamName(updateDto.getTeamName());
        memberTeamRepository.deleteAllByTeamExceptLeader(team, team.getLeader());
        //팀원들의 확인증 제거
        certificateRepository.deleteAllByTeamId(team.getId());
        certificateRepository.flush();
        //팀장의 확인증 재생성
        createAndSaveCertificate(team,contest,leader);
        //팀원 재생성
        addMembersToTeam(updateDto.getMemberIds(), team,contest);
    }

    @Transactional
    public void deleteTeam(Long teamId, String loginId, Long contestId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()->new IllegalArgumentException("해당 팀은 존재하지 않습니다."));
        team.isNotTeamLeader(team, loginId);

        //팀 삭제은 접수기간에만 가능하다.
        Contest contest = findContestById(contestId);
        validateContestJoin(contest);

        //팀장과 팀원들 확인증을 제거
        certificateRepository.deleteAllByTeamId(teamId);
        log.info("{} 팀(id:{})의 팀장과 팀원들의 예선 참가 확인증을 삭제합니다.",team.getName(),team.getId());
        memberTeamRepository.deleteAllByTeam(team);
        teamRepository.delete(team);
    }

    public List<MyTeamInfoDto> getMyTeamInfo(String loginId){
        Member member = memberRepository.findMemberByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀원은 존재하지 않습니다."));

        return teamRepository.findTeamByMemberLoginId(member.getLoginId())
                .stream()
                .map(team -> new MyTeamInfoDto(
                        team.getId(), team.getName(), team.getLeader().getLoginId(), team.getCreatedAt()
                )).toList();
    }

    public MyTeamInfoByContestDto getMyTeamInfoByContest(Long contestId, String loginId){
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(()->new IllegalArgumentException("해당 대회는 존재하지 않습니다."));
        Team team = teamRepository.findTeamByMemberAndContest(loginId,contest.getId())
                .orElseThrow(()->new IllegalArgumentException("해당하는 팀이 존재하지 않습니다."));
        List<MemberTeam> memberTeams = memberTeamRepository.findAllByTeamId(team.getId());
        List<Member> members = memberTeams.stream()
                .map(MemberTeam::getMember)
                .toList(); //본인이 속한 팀의 멤버들 리스트

        return MyTeamInfoByContestDto.of(team, members, contest);
    }

    private void addMembersToTeam(List<String> memberIds, Team team,Contest contest) {
        List<MemberTeam> memberTeams = new ArrayList<>();
        for (String loginId : memberIds) {
            Member member = memberRepository.findMemberByLoginId(loginId)
                    .orElseThrow(()->new IllegalArgumentException(String.format("%s는 존재하지 않는 계정입니다.",loginId)));
            boolean result = memberTeamRepository.existsByContestIdAndMemberId(contest.getId(), member.getId());
            log.info("중복 확인- 대회ID: {}, 멤버ID: {}, 로그인ID: {}, 중복여부: {}",
                    contest.getId(), member.getId(), loginId, result);
            if(result){
                //추가할려는 팀원이 같은 대회에 다른 팀에 속해있는 경우
                throw new ContestJoinException(String.format("%s님은 이미 해당 대회에 소속된 팀이 있습니다.",loginId));
            }
            //초등부/중등부/고등부/대학일반부에 따라 가입이 가능한지 아닌지 검증 함
            validateMemberDivision(team,member);
            teamJoinEligibilityPolicy.validate(member); //팀에 가입할 정책에 준수하는 유저인가(현재 : 매년 회원가입 정책)
            memberTeams.add(MemberTeam.of(member, team));
            createAndSaveCertificate(team, contest, member);//팀원의 확인증 만들기
        }
        memberTeamRepository.saveAll(memberTeams);
    }

    private void validateMemberDivision(final Team team, final Member member) {
        Organization organization = member.getOrganization();

        Division teamDivision = team.getDivision();
        StudentType studentType = null;

        if (organization instanceof School school) {
            studentType = school.getStudentType();

            Division expectedDivision = switch (studentType) {
                case ELEMENTARY -> Division.ELEMENTARY;//초등학생은 초등부
                case MIDDLE -> Division.MIDDLE;//중학생은 중등부
                case HIGH -> Division.HIGH;//고등학생은 고등부
                case COLLEGE -> Division.COLLEGE_GENERAL;//대학생은 대학 일반부
            };

            if (!teamDivision.equals(expectedDivision)) {
                throw new ContestJoinException(String.format(
                        "%s님은 %s에 들어갈 수 없습니다.",
                        member.getLoginId(), teamDivision.getDescription()
                ));
            }
        } else {
            // 직장인인 경우, 대학일반부만 가능
            if (!teamDivision.equals(Division.COLLEGE_GENERAL)) {
                throw new ContestJoinException(String.format(
                        "%s님은 대학일반부에 들어갈 수 없습니다.",
                        member.getLoginId()
                ));
            }
        }
    }


    private void createAndSaveCertificate(final Team team, final Contest contest, final Member member) {
        //팀원의 확인증 만들어 저장하기
        Certificate certificate = Certificate.createPreliminaryCertificate(UUID.randomUUID().toString(), contest, member, team);
        Certificate save = certificateRepository.save(certificate);
        log.info("[TeamService] {} 대회(id:{}) 참가한 팀(id:{})의 팀원({})의 \"{}\" 확인증을 저장",
                contest.getTitle(),contest.getId(),team.getId(),member.getLoginId(),save.getTitle());
    }

    private void validateTeamSize(List<String> memberIds) {
        if (memberIds.isEmpty() || memberIds.size() > 2) {
            throw new IllegalArgumentException("팀원은 최대 2명까지 등록할 수 있습니다.");
        }
    }

    private Contest findContestById(Long contestId) {
        return contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("현재 대회를 접수 할 수 없습니다."));
    }
}
