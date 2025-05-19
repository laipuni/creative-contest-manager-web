package com.example.cpsplatform.teamsolve.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.ContestJoinException;
import com.example.cpsplatform.exception.TemporaryAnswerNotFoundException;
import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.storage.FileStorage;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import com.example.cpsplatform.queue.job.AnswerSubmitJob;
import com.example.cpsplatform.queue.service.QueueService;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerDto;
import com.example.cpsplatform.teamsolve.controller.response.GetTeamAnswerResponse;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import com.example.cpsplatform.teamsolve.service.dto.FinalSubmitAnswerDto;
import com.example.cpsplatform.teamsolve.service.dto.SubmitAnswerDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerSubmitService {

    public static final String ANSWER_SUBMIT_LOG = "[답안 제출]";

    private final TeamRepository teamRepository;
    private final ProblemRepository problemRepository;
    private final TeamSolveRepository teamSolveRepository;
    private final FileStorage fileStorage;
    private final FileRepository fileRepository;
    private final ContestRepository contestRepository;
    private final QueueService queueService;
    private final EntityManager entityManager;

    /**
     * 조인 시 과도한 데이터 로드와 쿼리 복잡도를 피하기 위해
     * 여러 쿼리로 분리함
     *
     * @param contestId 답안지를 조회할 대회의 PK
     * @param loginId 답안지를 조회하는 유저의 ID
     * @return 팀 수정 횟수, 팀 제출 상태, 답안지 정보들
     */
    public GetTeamAnswerResponse getAnswer(Long contestId, String loginId,TeamSolveType teamSolveType){
        //해당 대회에 참여한 팀이 있는지 확인
        Team team = teamRepository.findTeamByMemberLoginIdAndContestId(loginId, contestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회에 참여한 팀이 없습니다."));
        log.info("대회 {}에 참여한 팀: {} 답안지 조회", contestId, team.getName());

        //해당 답안지와 문제의 정보를 dto로 조회
        List<GetTeamAnswerDto> response = teamSolveRepository.findSubmittedAnswersByTeamId(team.getId(),teamSolveType);
        findFileByTeamSolve(response);
        log.info("답안 조회 완료, teamId = {}, 답안 수={}", team.getId(), response.size());
        return new GetTeamAnswerResponse(team.getFinalSubmitCount(), team.getStatus(), response);
    }

    private void findFileByTeamSolve(final List<GetTeamAnswerDto> response) {
        for(GetTeamAnswerDto dto : response){
            //한 번에 조회하는 것보다 개별 조회가 가독성과 처리에 유리하여 파일 단위로 조회함
            fileRepository.findFileByTeamSolveId(dto.getTeamSolveId())
                    .ifPresent(dto::setFileInfo);
        }
    }

    /**
     * 최종적으로 답안지 파일을 제출하는 로직
     * 임시 저장한 답안지 -> 최종 답안지로 바꾸고,
     * 이전 최종 답안지는 삭제한다.
     *
     * @param answerDto 답안지 제출 과정에 필요한 대회,팀, 답안지를 제출하는 유저의 pk가 있음
     */
    @Transactional
    public void submitAnswerComplete(FinalSubmitAnswerDto answerDto){
        Contest contest = validateContest(answerDto.getContestId(), answerDto.getNow()); //현재 대회 개최 시간인지 검증
        Team team = validateTeamLeader(answerDto.getLoginId(), answerDto.getContestId()); //팀장인지 검증
        team.finalSubmit();
        //최종 제출한 답안지가 있는지 확인 후 있다면 제거
        deleteFinalTeamSolvesAndFiles(team, contest);
        //임시 저장한 답안지를 불러옴
        List<TeamSolve> tempTeamSolves = findTempTeamSolve(team.getId(), contest.getId());
        //임시 저장할 답안지를 최종 제출로 바꾸기
        tempTeamSolves.forEach(TeamSolve::submit);
    }

    private void deleteFinalTeamSolvesAndFiles(final Team team, final Contest contest) {
        List<TeamSolve> finalTeamSolves = teamSolveRepository.findAllByTeamIdAndContestIdAndTeamSolveType(
                team.getId(),
                contest.getId(),
                TeamSolveType.SUBMITTED
        );
        List<Long> finalTeamSolveIds = finalTeamSolves.stream().map(TeamSolve::getId).toList();
        List<File> finalTeamSolveFiles = fileRepository.findAllByTeamSolve_IdIn(finalTeamSolveIds);
        fileRepository.hardDeleteAllByIdIn(finalTeamSolveFiles.stream().map(File::getId).toList());
        teamSolveRepository.deleteAllByIdInBatch(finalTeamSolveIds);
        //영속성 컨텍스트를 refresh하여 DB 상태와 동기화
        entityManager.flush();
        entityManager.clear();
    }

    private List<TeamSolve> findTempTeamSolve(final Long teamId, final Long contestId) {
        //임시 저장 답안지가 있는지 조회한다. 없다면 예외
        List<TeamSolve> tempTeamSolves = teamSolveRepository.findAllByTeamIdAndContestIdAndTeamSolveType(
                teamId,
                contestId,
                TeamSolveType.TEMP
        );
        if(tempTeamSolves.isEmpty()){
            //만약 최종 제출할 임시 답안들이 없을 경우, 예외 발생
            throw new TemporaryAnswerNotFoundException();
        }
        return tempTeamSolves;
    }

    /**
     * 임시로 답안지 파일을 제출하는 로직
     * @param fileSource 올릴 답안지 파일의 메타 데이터
     * @param answerDto 답안지 제출 과정에 필요한 대회,팀,올리는 유저의 pk가 있음
     */
    @Transactional
    public void submitAnswerTemporary(FileSource fileSource, SubmitAnswerDto answerDto){
        Team team = uploadAndSaveTeamSolve(fileSource, answerDto, TeamSolveType.TEMP);
        //팀의 제출 상태를 임시 제출로 변경
        if(team.isNotFinalSubmit()){
            //최종 제출이 아닐 경우 팀 제출 상태를 임시 제출로 변경
            team.temporarySubmit();
        }
    }

    /**
     * 제출 시간과 팀장이 제출하는지 검증한다.
     *
     * @param fileSource 올릴 답안지 파일의 메타 데이터
     * @param answerDto  답안지 제출 과정에 필요한 대회,팀,올리는 유저의 pk가 있음
     * @param teamSolveType 팀 제출 타입(임시, 최종)
     * @return 팀의 상태를 바꾸는 작업을 위해서 제출한 팀을 반환함
     */
    public Team uploadAndSaveTeamSolve(FileSource fileSource, SubmitAnswerDto answerDto, final TeamSolveType teamSolveType){
        Contest contest = validateContest(answerDto.getContestId(), answerDto.getNow()); //현재 대회 개최 시간인지 검증
        Team team = validateTeamLeader(answerDto.getLoginId(), answerDto.getContestId()); //팀장인지 검증
        Problem problem = problemRepository.findById(answerDto.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 대회 문제는 존재하지 않습니다."));

        String path = generateTeamSolvePath(contest,problem);
        if(fileSource != null){
            //제출할 파일이 존재할 경우 업로드
            fileStorage.upload(path,fileSource);
        }
        log.info("{} 유저(loginId:{})가 팀(id:{}) 문제(id:{})의 답안지 업로드",
                ANSWER_SUBMIT_LOG, answerDto.getLoginId(),team.getId(),problem.getId());

        saveTeamSolve(AnswerSubmitJob.of(teamSolveType, team.getId(),problem.getId(),fileSource,path,answerDto.getContent()));
        return team;
    }

    private String generateTeamSolvePath(final Contest contest,final Problem problem){
        //ex) /answer/16회/고등-일반/1번
        return String.format("/answer/%d/%s/%d번",contest.getSeason(),problem.getSection().getLabel(),problem.getProblemOrder());
    }

    private Team validateTeamLeader(final String loginId, final Long contestId) {
        log.info("트랜잭션 시작 - Thread: {}", Thread.currentThread().getName());
        log.debug("{} 유저(id:{})가 대회(id:{})에 팀장인지 확인",ANSWER_SUBMIT_LOG, loginId,contestId);
        Team team = teamRepository.findTeamByContestIdAndLeaderIdWithLock(contestId, loginId)
                .orElseThrow(() -> new ContestJoinException("답안지는 팀장만이 제출할 수 있습니다."));
        log.info("트랜잭션 종료 - Thread: {}", Thread.currentThread().getName());
        return team;
    }

    private Contest validateContest(final Long contestId, final LocalDateTime now) {
        //콘테스트 조회 후 현재 제출 시간이 맞는지 확인
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestJoinException("답을 제출할 대회가 존재하지 않습니다."));
        if(contest.isNotOngoing(now)){
            //만약 대회 진행 시간이 아닌 경우
            throw new ContestJoinException("현재 대회시간이 아니라 답을 제출할 수 없습니다.");
        }
        return contest;
    }

    @Transactional
    public void saveTeamSolve(final AnswerSubmitJob job) {
        Team team = findTeam(job.getTeamId());
        Problem problem = findProblems(job.getProblemId());
        TeamSolve teamSolve = findOrCreateTeamSolve(problem, team, job.getContent(),job.getTeamSolveType());
        if(job.getFileSource() != null){
            //파일을 덮어서 다시 올릴 경우
            File file = createFile(job.getFileSource(), job.getPath(), teamSolve);
            removeAndSaveAllFile(teamSolve.getId(), file);
            log.info("{} 팀 답안 및 파일 저장 완료, teamId: {}, 문제: {}", ANSWER_SUBMIT_LOG , team.getId(), problem.getId());
        }
    }

    private Team findTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀을 찾을 수 없습니다."));
    }

    private Problem findProblems(Long problemId) {
        Optional<Problem> result = problemRepository.findById(problemId);
        if(result.isEmpty()){
            log.error("해당 문제(id:{})를 찾을 수 없습니다.",problemId);
            throw new IllegalStateException(
                    String.format("해당 문제(%d)를 찾지 못해 답안지 파일 저장에 실패했습니다.",problemId)
            );
        }
        return result.get();
    }

    private void removeAndSaveAllFile(final Long teamSolveId, final File file) {
        log.info("{} 이전 답안 파일 삭제 중, teamSolveIds: {}", ANSWER_SUBMIT_LOG , teamSolveId);
        int result = fileRepository.hardDeleteAllByTeamSolveIdIn(List.of(teamSolveId));
        log.info("{} 이전 파일 삭제 완료, 삭제된 파일 수: {}", ANSWER_SUBMIT_LOG , result);
        File save = fileRepository.save(file);
        log.info("{} 새로 제출된 답안 파일 저장 완료, 파일 Id: {}", ANSWER_SUBMIT_LOG , save.getId());
    }

    private File createFile(final FileSource fileSource, final String path, final TeamSolve teamSolve) {
        return File.createProblemAnswerFile(
                fileSource.getUploadFileName(),
                fileSource.getOriginalFilename(),
                fileSource.getExtension(),
                fileSource.getMimeType(),
                fileSource.getSize(),
                path,
                teamSolve
        );
    }

    private TeamSolve findOrCreateTeamSolve(final Problem problem, final Team team,final String content,final TeamSolveType teamSolveType) {
        Optional<TeamSolve> teamSolveOptional = teamSolveRepository.findByTeamIdAndProblemId(team.getId(), problem.getId(),teamSolveType);

        if (teamSolveOptional.isEmpty()) {
            //첫 답안지 제출일 경우
            log.info("{} 팀 {} 답안 생성 - teamId: {}, problemId: {}", ANSWER_SUBMIT_LOG,teamSolveType.getLabel(), team.getId(), problem.getId());
            return teamSolveRepository.save(TeamSolve.of(team, problem,content, teamSolveType));
        } else {
            //이전에 제출한 답안지 정보가 존재할 경우
            TeamSolve teamSolve = teamSolveOptional.get();
            teamSolve.modifyContent(content);
            log.info("{} 기존 팀 {} 답안 수정 - teamId: {}, problemId: {}, 문제 풀이 = {}"
                    , ANSWER_SUBMIT_LOG,teamSolveType.getLabel(), team.getId(), problem.getId(),teamSolve.getContent());
            return teamSolve;
        }
    }
}
