package com.example.cpsplatform.teamsolve.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.exception.ContestJoinException;
import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileType;
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
import com.example.cpsplatform.teamsolve.repository.TeamSolveRepository;
import com.example.cpsplatform.teamsolve.service.dto.SubmitAnswerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    /*
     * 한번의 쿼리가 아닌 여러번의 쿼리로 나눈 이유는
     * 조인이 많으면 테이블에 불러오는 데이터가 많고.
     * 쿼리가 복잡해지기 때문에 분리했음
     */
    public GetTeamAnswerResponse getAnswer(Long contestId, String loginId){
        //해당 대회에 참여한 팀이 있는지 확인
        Team team = teamRepository.findTeamByMemberLoginIdAndContestId(loginId, contestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회에 참여한 팀이 없습니다."));
        log.info("대회 {}에 참여한 팀: {} 답안지 조회", contestId, team.getName());

        //해당 답안지와 문제의 정보를 dto로 조회
        List<GetTeamAnswerDto> response = teamSolveRepository.findSubmittedAnswersByTeamId(team.getId());
        findFileByTeamSolve(response);
        log.info("답안 조회 완료, teamId = {}, 답안 수={}", team.getId(), response.size());
        return new GetTeamAnswerResponse(response);
    }

    private void findFileByTeamSolve(final List<GetTeamAnswerDto> response) {
        for(GetTeamAnswerDto dto : response){
            //한 번에 조회하는 것보다 개별 조회가 가독성과 처리에 유리하여 파일 단위로 조회함
            fileRepository.findFileByTeamSolveId(dto.getTeamSolveId())
                    .ifPresent(dto::setFileInfo);
        }
    }

    public void submitAnswer(FileSource fileSource, SubmitAnswerDto answerDto){
        Contest contest = validateContest(answerDto);
        Team team = validateTeamLeader(answerDto);
        Problem problem = problemRepository.findById(answerDto.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 대회 문제는 존재하지 않습니다."));
        String path = generateTeamSolvePath(contest,problem);
        fileStorage.upload(path,fileSource);
        log.info("{} 유저(loginId:{})가 팀(id:{}) 문제(id:{})의 답안지 업로드",
                ANSWER_SUBMIT_LOG, answerDto.getLoginId(),team.getId(),problem.getId());
        queueService.enqueue(AnswerSubmitJob.of(team.getId(),problem.getId(),fileSource,path));
    }

    private String generateTeamSolvePath(final Contest contest,final Problem problem){
        //ex) /answer/16회/고등-일반/1번
        return String.format("/answer/%d/%s/%d번",contest.getSeason(),problem.getSection().getLabel(),problem.getProblemOrder());
    }

    private Team validateTeamLeader(final SubmitAnswerDto answerDto) {
        log.debug("{} 유저(id:{})가 대회(id:{})에 팀장인지 확인",ANSWER_SUBMIT_LOG, answerDto.getLoginId(),answerDto.getContestId());
        return teamRepository.findTeamByContestIdAndLeaderId(answerDto.getContestId(), answerDto.getLoginId())
                .orElseThrow(() -> new ContestJoinException("답안지는 팀장만이 제출할 수 있습니다."));
    }

    private Contest validateContest(final SubmitAnswerDto answerDto) {
        //콘테스트 조회 후 현재 제출 시간이 맞는지 확인
        Contest contest = contestRepository.findById(answerDto.getContestId())
                .orElseThrow(() -> new ContestJoinException("답을 제출할 대회가 존재하지 않습니다."));
        if(contest.isNotOngoing(answerDto.getNow())){
            //만약 대회 진행 시간이 아닌 경우
            throw new ContestJoinException("현재 대회시간이 아니라 답을 제출할 수 없습니다.");
        }
        return contest;
    }

    @Transactional
    public void saveTeamSolve(final AnswerSubmitJob job) {
        Team team = findTeam(job.getTeamId());
        Problem problem = findProblems(job.getProblemId());
        TeamSolve teamSolve = findOrCreateTeamSolve(problem, team);
        File file = createFile(job.getFileSource(), job.getPath(), teamSolve);
        removeAndSaveAllFile(teamSolve.getId(), file);
        log.info("{} 팀 답안 및 파일 저장 완료, teamId: {}, 문제: {}", ANSWER_SUBMIT_LOG , team.getId(), problem.getId());
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
        int result = fileRepository.softDeletedByTeamSolveIdList(List.of(teamSolveId));
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
                FileType.TEAM_SOLUTION,
                teamSolve
        );
    }

    private TeamSolve findOrCreateTeamSolve(final Problem problem, final Team team) {
        Optional<TeamSolve> teamSolveOptional = teamSolveRepository.findByTeamIdAndProblemId(team.getId(), problem.getId());

        if (teamSolveOptional.isEmpty()) {
            log.info("{} 새 팀 답안 생성 - teamId: {}, problemId: {}", ANSWER_SUBMIT_LOG, team.getId(), problem.getId());
            return teamSolveRepository.save(TeamSolve.of(team, problem));
        } else {
            TeamSolve teamSolve = teamSolveOptional.get();
            teamSolve.incrementModifyCount();
            log.info("{} 기존 팀 답안 수정 - teamId: {}, problemId: {}, 수정 횟수: {}"
                    , ANSWER_SUBMIT_LOG, team.getId(), problem.getId(), teamSolve.getModifyCount());
            return teamSolve;
        }
    }
}
