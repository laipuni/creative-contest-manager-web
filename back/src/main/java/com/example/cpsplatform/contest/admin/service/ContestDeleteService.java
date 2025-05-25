package com.example.cpsplatform.contest.admin.service;


import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.repository.CertificateRepository;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamnumber.domain.TeamNumber;
import com.example.cpsplatform.teamnumber.repository.TeamNumberRepository;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestDeleteService {

    public static final String CONTEST_DELETE_LOG = "[ContestDelete] ";

    private final ContestRepository contestRepository;
    private final TeamRepository teamRepository;
    private final TeamNumberRepository teamNumberRepository;
    private final TeamSolveRepository teamSolveRepository;
    private final CertificateRepository certificateRepository;
    private final FileRepository fileRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final ProblemRepository problemRepository;
    private final EntityManager entityManager;

    @Transactional
    public void deleteCompletelyContest(final Long contestId) {
        log.info("{} 대회(id:{}) 완전 삭제 프로세스 시작", CONTEST_DELETE_LOG, contestId);
        //관련 데이터 조회
        List<Long> teamIds = findTeamIdsByContestId(contestId);
        List<Long> memberTeamIds = findMemberTeamIdsByTeamIds(teamIds);
        List<Long> teamSolveIds = findTeamSolveIdsByTeamIds(teamIds);
        List<Long> teamSolveFileIds = findTeamSolveFileIdsByTeamSolveIds(teamSolveIds);
        List<Long> problemIds = findProblemIdsByContestId(contestId);
        List<Long> problemFileIds = findProblemFileIdsByProblemIds(problemIds);
        List<Long> certificateIds = findCertificateIdsByContestId(contestId);
        Optional<TeamNumber> optionalTeamNumber = findTeamNumberByContestId(contestId);

        //deleteAllInBatch()는 영속성 컨텍스트를 무시하고 삭제하기에, 영속성 컨텍스트를 비우고 삭제
        entityManager.flush();
        entityManager.clear();

        //삭제 순서 (외래 키 제약조건 고려)
        deleteCertificates(contestId, certificateIds);
        deleteMemberTeams(contestId, memberTeamIds);
        deleteTeamSolveFiles(contestId, teamSolveFileIds);
        deleteProblemFiles(contestId, problemFileIds);
        deleteTeamSolves(contestId, teamSolveIds);
        deleteProblems(contestId, problemIds);
        deleteTeams(contestId, teamIds);
        deleteTeamNumber(contestId, optionalTeamNumber);

        //강제하지 않으면 네이티브 쿼리 하드 삭제로 contest가 가장먼저 삭제되는 일이 발생함
        //따라서 참조 무결성 제약조건 위반을 막기 위해 강제함
        deleteContest(contestId);

        log.info("{} 대회(id:{}) 완전 삭제 프로세스 종료", CONTEST_DELETE_LOG, contestId);
    }

    private List<Long> findTeamIdsByContestId(Long contestId) {
        List<Team> teams = teamRepository.findAllByContestIdNative(contestId);
        return teams.stream().map(Team::getId).toList();
    }

    private List<Long> findMemberTeamIdsByTeamIds(List<Long> teamIds) {
        List<MemberTeam> memberTeams = memberTeamRepository.findAllByTeamIds(teamIds);
        return memberTeams.stream().map(MemberTeam::getId).toList();
    }

    private List<Long> findTeamSolveIdsByTeamIds(List<Long> teamIds) {
        if (teamIds.isEmpty()) {
            return List.of();
        }
        List<TeamSolve> teamSolves = teamSolveRepository.findAllByTeam_IdInNative(teamIds);
        return teamSolves.stream().map(TeamSolve::getId).toList();
    }

    private List<Long> findTeamSolveFileIdsByTeamSolveIds(List<Long> teamSolveIds) {
        if (teamSolveIds.isEmpty()) {
            return List.of();
        }
        List<File> teamSolveFiles = fileRepository.findAllByTeamSolve_IdInNative(teamSolveIds);
        return teamSolveFiles.stream().map(File::getId).toList();
    }

    private List<Long> findProblemFileIdsByProblemIds(List<Long> problemIds) {
        if (problemIds.isEmpty()) {
            return List.of();
        }
        List<File> problemFiles = fileRepository.findAllByProblem_IdInNative(problemIds);
        return problemFiles.stream().map(File::getId).toList();
    }

    private List<Long> findProblemIdsByContestId(Long contestId) {
        List<Problem> problems = problemRepository.findAllByContestIdNative(contestId);
        return problems.stream().map(Problem::getId).toList();
    }

    private List<Long> findCertificateIdsByContestId(Long contestId) {
        List<Certificate> certificates = certificateRepository.findAllByContestIdNative(contestId);
        return certificates.stream().map(Certificate::getId).toList();
    }

    private Optional<TeamNumber> findTeamNumberByContestId(Long contestId) {
        return teamNumberRepository.findByContestIdNative(contestId);
    }

    private void deleteTeamSolveFiles(Long contestId, List<Long> teamSolveFileIds) {
        if (!teamSolveFileIds.isEmpty()) {
            log.info("{} 대회(id:{})와 관련된 팀 답안지 파일 ID:{} {}개 삭제",
                    CONTEST_DELETE_LOG, contestId, teamSolveFileIds, teamSolveFileIds.size());
            fileRepository.hardDeleteAllByIdIn(teamSolveFileIds);
        }
    }

    private void deleteProblemFiles(Long contestId, List<Long> problemFileIds) {
        if (!problemFileIds.isEmpty()) {
            log.info("{} 대회(id:{})와 관련된 문제 파일 ID:{} {}개 삭제",
                    CONTEST_DELETE_LOG, contestId, problemFileIds, problemFileIds.size());
            fileRepository.hardDeleteAllByIdIn(problemFileIds);
        }
    }

    private void deleteCertificates(Long contestId, List<Long> certificateIds) {
        if (!certificateIds.isEmpty()) {
            log.info("{} 대회(id:{})와 관련된 확인서 ID:{} {}개 삭제",
                    CONTEST_DELETE_LOG, contestId, certificateIds, certificateIds.size());
            certificateRepository.deleteAllByIdInBatch(certificateIds);
        }
    }

    private void deleteTeamSolves(Long contestId, List<Long> teamSolveIds) {
        if (!teamSolveIds.isEmpty()) {
            log.info("{} 대회(id:{})와 관련된 팀 답안지 정보 ID:{} {}개 삭제",
                    CONTEST_DELETE_LOG, contestId, teamSolveIds, teamSolveIds.size());
            teamSolveRepository.deleteAllByIdInBatch(teamSolveIds);
        }
    }

    private void deleteProblems(Long contestId, List<Long> problemIds) {
        if (!problemIds.isEmpty()) {
            log.info("{} 대회(id:{})와 관련된 문제 ID:{} {}개 삭제",
                    CONTEST_DELETE_LOG, contestId, problemIds, problemIds.size());
            problemRepository.deleteAllByIdInBatch(problemIds);
        }
    }

    private void deleteMemberTeams(Long contestId, List<Long> memberTeamIds) {
        if (!memberTeamIds.isEmpty()) {
            log.info("{} 대회(id:{})와 관련된 팀원 정보 ID:{} {}개 삭제",
                    CONTEST_DELETE_LOG, contestId, memberTeamIds, memberTeamIds.size());
            memberTeamRepository.deleteAllByIdInBatch(memberTeamIds);
        }
    }

    private void deleteTeams(Long contestId, List<Long> teamIds) {
        if (!teamIds.isEmpty()) {
            log.info("{} 대회(id:{})와 관련된 팀 ID:{} {}개 삭제",
                    CONTEST_DELETE_LOG, contestId, teamIds, teamIds.size());
            teamRepository.deleteAllByIdInBatch(teamIds);
        }
    }

    private void deleteTeamNumber(Long contestId, Optional<TeamNumber> optionalTeamNumber) {
        if (optionalTeamNumber.isPresent()) {
            TeamNumber teamNumber = optionalTeamNumber.get();
            log.info("{} 대회(id:{})와 관련된 팀 접수 번호 데이터 ID:{} 삭제",
                    CONTEST_DELETE_LOG, contestId, teamNumber.getId());
            teamNumberRepository.deleteAllByIdInBatch(List.of(teamNumber.getId()));
        }
    }

    private void deleteContest(Long contestId) {
        log.info("{} 대회(id:{}) 삭제", CONTEST_DELETE_LOG, contestId);
        contestRepository.hardDeleteById(contestId);
    }
}
