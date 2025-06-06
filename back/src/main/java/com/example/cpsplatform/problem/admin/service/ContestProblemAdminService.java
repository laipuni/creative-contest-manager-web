package com.example.cpsplatform.problem.admin.service;


import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.service.dto.FileSaveDto;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.storage.FileStorage;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemDetailResponse;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemListResponse;
import com.example.cpsplatform.problem.admin.service.dto.AddProblemDto;
import com.example.cpsplatform.problem.admin.service.dto.UpdateProblemDto;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestProblemAdminService {

    public static final int PROBLEM_SIZE = 10;
    private static final String CONTEST_FILE_PATH_FORMAT = "/contest/%d회/%s/%d번";
    private static final String LOG_PREFIX = "[ContestProblemAdmin]";

    private final FileStorage fileStorage;
    private final FileRepository fileRepository;
    private final ProblemRepository problemRepository;
    private final ContestRepository contestRepository;

    @Transactional
    public void addContestProblem(AddProblemDto addProblemDto, FileSources fileSources) {
        Contest contest = findContest(addProblemDto.getContestId());//콘테스트 조회
        Problem problem = saveProblem(addProblemDto, contest); //문제 등록
        String path = generateContestFilePath( //파일경로 생성
                contest.getSeason(),
                addProblemDto.getSection(),
                problem.getProblemOrder()
        );
        if(fileSources.hasFileSource()) { //올릴 파일이 존재할 경우 업로드
            fileStorage.upload(path, fileSources);
            processAndUploadFiles(fileSources,path,problem);
        }
    }

    @Transactional
    public void updateContestProblem(UpdateProblemDto problemDto , FileSources fileSources) {
        Contest contest = findContest(problemDto.getContestId());
        Problem problem = updateProblem(problemDto);
        String path = generateContestFilePath(
                contest.getSeason(),
                problemDto.getSection(),
                problem.getProblemOrder()
        );
        if(fileSources.hasFileSource()) { //올릴 파일이 존재할 경우
            fileStorage.upload(path, fileSources);
            processAndUploadFiles(fileSources,path,problem);
        }
        if(problemDto.hasDeleteFile()){ //제거할 파일이 있는 경우 삭제
            deleteFiles(problemDto, path, problem);
        }
    }

    private Problem updateProblem(final UpdateProblemDto problemDto) {
        Problem problem = findProblem(problemDto);
        problem.updateContestProblem(
                problemDto.getTitle(),
                problemDto.getSection(),
                problemDto.getContent(),
                problemDto.getProblemOrder()
        );
        return problem;
    }

    private void deleteFiles(final UpdateProblemDto problemDto, final String path, final Problem problem) {
        List<File> files = fileRepository.findAllById(problemDto.getDeleteFileIds());
        files.forEach((file) -> {
            fileStorage.delete(path, file.getName());
            problem.removeFile(file);
        });
    }
    public ContestProblemListResponse findContestProblemList(final Long contestId, final int page) {
        Pageable pageable = PageRequest.of(page,PROBLEM_SIZE);
        log.debug("대회 ({})의 출제 문제(page ={}) 조회 시도",contestId,page);
        Page<Problem> result = problemRepository.findContestProblemsByContestAndProblemType(pageable, ProblemType.CONTEST, contestId);
        return ContestProblemListResponse.of(result);
    }
    public ContestProblemDetailResponse findContestProblemDetail(final Long contestId, final Long problemId) {
        log.debug("대회 ({})의 출제 문제({})를 조회 시도",contestId,problemId);
        Problem problem = problemRepository.findContestProblemByContestIdAndProblemId(ProblemType.CONTEST, problemId, contestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제는 존재하지 않습니다."));
        log.debug("대회 ({})에 출제 문제({})의 파일 조회 시도",contestId,problemId);
        List<File> files = fileRepository.findAllByProblemIdAndFileTypeAndNotDeleted(problemId, FileType.PROBLEM_REAL);
        return ContestProblemDetailResponse.of(problem,files);
    }

    @Transactional
    public void deleteContestProblem(final Long deleteProblemId) {
        log.debug("문제({})와 관련된 파일 삭제 시도.",deleteProblemId);
        fileRepository.softDeletedByProblemId(deleteProblemId);
        problemRepository.deleteById(deleteProblemId);
        log.info("문제({})와 관련된 파일들을 삭제했습니다.",deleteProblemId);
    }

    private Problem saveProblem(final AddProblemDto addProblemDto, final Contest contest) {
        Problem problem = problemRepository.save(addProblemDto.createContestProblem(contest));
        log.info("{} 문제 저장 문제 Id = {}, 제목 = {}, 섹션 = {}, 문제 번호 = {}", LOG_PREFIX,problem.getId(), problem.getTitle(),
                problem.getSection().getLabel(), problem.getProblemOrder());
        return problem;
    }

    private void processAndUploadFiles(FileSources fileSources, String path, Problem problem) {
        List<FileSaveDto> fileSaveDtos = fileSources.toFileSaveDtos(path,FileType.PROBLEM_REAL);
        List<File> files = fileSaveDtos.stream()
                .map((fileSaveDto) -> fileSaveDto.createContestProblemFile(problem))
                .toList();
        List<File> result = fileRepository.saveAll(files);
        log.info("{} 파일 ({}) 저장 완료",LOG_PREFIX, result.stream().map(File::getId).toList());
    }

    private Contest findContest(Long contestId) {
        log.debug("{} 대회 조회 {} ",LOG_PREFIX, contestId);
        return contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 등록할 대회가 존재하지 않습니다."));
    }

    private String generateContestFilePath(int season, Section section, int problemOrder){
        return String.format(CONTEST_FILE_PATH_FORMAT,season,section.getLabel(),problemOrder);
    }

    private Problem findProblem(final UpdateProblemDto problemDto) {
        return problemRepository.findById(problemDto.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 문제는 존재하지 않습니다."));
    }

}
