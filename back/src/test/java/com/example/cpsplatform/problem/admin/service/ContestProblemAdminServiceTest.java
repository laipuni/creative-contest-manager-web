package com.example.cpsplatform.problem.admin.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.storage.FileStorage;
import com.example.cpsplatform.problem.admin.controller.response.ContestProblemDetailResponse;
import com.example.cpsplatform.problem.admin.service.dto.AddProblemDto;
import com.example.cpsplatform.problem.admin.service.dto.UpdateProblemDto;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class ContestProblemAdminServiceIntegrationTest {

    @Autowired
    private ContestProblemAdminService contestProblemAdminService;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private FileRepository fileRepository;

    @MockitoBean
    private FileStorage fileStorage;

    @Autowired
    EntityManager entityManager;

    @DisplayName("콘테스트 문제를 추가한다.")
    @Test
    void addContestProblem() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt= now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);
        Contest contest = Contest.builder()
                .season(16)
                .title("16회 창의력 경진대회 대회")
                .description("16회 창의력 경진대회 대회 설명")
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();

        Contest savedContest = contestRepository.save(contest);

        String title = "문제 1번";
        String content = "문제 설명";
        int problemOrder = 3;
        AddProblemDto addProblemDto = AddProblemDto.builder()
                .contestId(savedContest.getId())
                .section(Section.COMMON)
                .problemOrder(problemOrder)
                .title(title)
                .content(content)
                .build();

        String originalFilename1 = "문제1_1.pdf";
        FileSource fileSource1 = new FileSource(
                "upload1.pdf",
                originalFilename1,
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        String originalFilename2 = "문제1_2.pdf";
        FileSource fileSource2 = new FileSource(
                "upload2.pdf",
                originalFilename2,
                new byte[]{4, 5, 6},
                "application/pdf",
                FileExtension.PDF,
                200L
        );

        List<FileSource> fileSourceList = List.of(fileSource1, fileSource2);
        FileSources fileSources = FileSources.of(fileSourceList);

        String expectedPath = "/contest/16회/공통/3번";

        // when
        contestProblemAdminService.addContestProblem(addProblemDto, fileSources);
        List<File> savedFiles = fileRepository.findAll();
        List<Problem> problems = problemRepository.findAll();

        //then
        assertThat(problems.get(0)).isNotNull()
                .extracting("title","content","problemOrder","contest")
                .containsExactly(title,content,problemOrder,contest);
        assertThat(savedFiles).hasSize(2)
                .extracting("originalName")
                .containsExactlyInAnyOrder(originalFilename1, originalFilename2);

        //파일 스토리지에 업로드 요청이 갔는지 확인
        verify(fileStorage, times(1)).upload(eq(expectedPath), any(FileSources.class));
    }

    @Test
    @DisplayName("존재하지 않는 콘테스트의 문제 추가 시 예외 발생한다.")
    void addContestProblemWithNonExistContest() {
        // given
        Long nonExistingContestId = 999L;

        String title = "문제 1번";
        String content = "문제 설명";
        int problemOrder = 3;
        AddProblemDto addProblemDto = AddProblemDto.builder()
                .contestId(nonExistingContestId)
                .section(Section.COMMON)
                .problemOrder(problemOrder)
                .title(title)
                .content(content)
                .build();

        FileSource fileSource = new FileSource(
                "upload.pdf",
                "sample.pdf",
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        FileSources fileSources = FileSources.of(List.of(fileSource));

        //when
        //then
        assertThatThrownBy(() -> contestProblemAdminService.addContestProblem(addProblemDto, fileSources))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("문제를 등록할 대회가 존재하지 않습니다.");
    }

    @DisplayName("출제 문제 상세 조회 할 때, 문제가 존재하지 않으면 예외 발생한다.")
    @Test
    void findContestProblemDetailWithNotExistProblem(){
        //given
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("테스트 대회 설명")
                .season(16)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();
        contestRepository.save(contest);


        Problem problem = Problem.builder()
                .title("문제 제목")
                .contest(contest)
                .section(Section.COMMON)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();
        problemRepository.save(problem);
        Long invalidContestId = 2025L;

        //when
        //then
        assertThatThrownBy(() -> contestProblemAdminService.findContestProblemDetail(invalidContestId,problem.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 문제는 존재하지 않습니다.");
    }

    @DisplayName("출제 문제 상세 조회 할 때, 파일이 없을 경우 파일 정보 없이 반환한다.")
    @Test
    void findContestProblemDetailWithEmptyFileList(){
        //given
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("테스트 대회 설명")
                .season(16)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();
        contestRepository.save(contest);

        Problem problem = Problem.builder()
                .title("문제 제목")
                .contest(contest)
                .section(Section.COMMON)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();
        problemRepository.save(problem);

        //when
        List<Problem> all = problemRepository.findAll();
        List<Contest> all1 = contestRepository.findAll();
        ContestProblemDetailResponse result = contestProblemAdminService.findContestProblemDetail(contest.getId(), problem.getId());
        //then
        assertThat(result.getFileList()).isEmpty();
    }

    @DisplayName("출제 문제 상세 조회 한다.")
    @Test
    void findContestProblemDetail(){
        //given
        Contest contest = Contest.builder()
                .title("테스트 대회")
                .description("테스트 대회 설명")
                .season(16)
                .registrationStartAt(now().minusDays(5))
                .registrationEndAt(now().minusDays(1))
                .startTime(now())
                .endTime(now().plusDays(1))
                .build();
        contestRepository.save(contest);

        Problem problem = Problem.builder()
                .title("문제 제목")
                .contest(contest)
                .section(Section.COMMON)
                .problemOrder(1)
                .problemType(ProblemType.CONTEST)
                .content("문제 설명")
                .build();
        problemRepository.save(problem);

        String name = "문제1_1.pdf";
        String originalName = "문제1_1.pdf";
        FileType problemReal = FileType.PROBLEM_REAL;
        String mimeType = FileExtension.PDF.getMimeType();
        long size = 100L;
        String path = "/contest/16회/공통/1번";
        File file = File.builder()
                .problem(problem)
                .name(name)
                .originalName(originalName)
                .fileType(problemReal)
                .mimeType(mimeType)
                .extension(FileExtension.PDF)
                .size(size)
                .path(path)
                .build();
        fileRepository.save(file);

        //when
        ContestProblemDetailResponse result = contestProblemAdminService.findContestProblemDetail(contest.getId(), problem.getId());

        //then
        assertThat(result).isNotNull()
                .extracting("problemId","title","season","section","content","problemType","problemOrder")
                .containsExactly(
                        problem.getId(),
                        problem.getTitle(),
                        contest.getSeason(),
                        problem.getSection(),
                        problem.getContent(),
                        problem.getProblemType(),
                        problem.getProblemOrder()
                );
        assertThat(result.getFileList().get(0)).isNotNull()
                .extracting("fileId","originalFileName","createAt")
                .containsExactly(file.getId(),originalName,file.getCreatedAt());
    }

    @DisplayName("콘테스트 문제를 수정한다.")
    @Test
    void updateContestProblem() {
        //given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registrationStartAt = now.plusDays(1);
        LocalDateTime registrationEndAt = now.plusDays(2);
        LocalDateTime contestStartAt = now.plusDays(3);
        LocalDateTime contestEndAt = now.plusDays(4);

        Contest contest = Contest.builder()
                .season(16)
                .title("16회 창의력 경진대회 대회")
                .description("16회 창의력 경진대회 대회 설명")
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(contestStartAt)
                .endTime(contestEndAt)
                .build();

        Contest savedContest = contestRepository.save(contest);

        //기존 문제 생성
        String originalTitle = "기존 문제";
        String originalContent = "기존 문제 설명";
        int originalProblemOrder = 1;
        Section originalSection = Section.COMMON;

        Problem problem = Problem.builder()
                .title(originalTitle)
                .content(originalContent)
                .problemType(ProblemType.CONTEST)
                .problemOrder(originalProblemOrder)
                .section(originalSection)
                .contest(savedContest)
                .build();

        Problem savedProblem = problemRepository.save(problem);

        //기존 파일 생성
        String path = "/contest/16회/공통/1번";
        String originalFilename1 = "기존파일1.pdf";
        String originalFilename2 = "기존파일2.pdf";

        File file1 = File.builder()
                .originalName(originalFilename1)
                .name("saved1.pdf")
                .extension(FileExtension.PDF)
                .size(100L)
                .fileType(FileType.PROBLEM_REAL)
                .problem(savedProblem)
                .mimeType(FileExtension.PDF.getMimeType())
                .path(path)
                .build();

        File file2 = File.builder()
                .originalName(originalFilename2)
                .name("saved2.pdf")
                .extension(FileExtension.PDF)
                .size(200L)
                .fileType(FileType.PROBLEM_REAL)
                .problem(savedProblem)
                .mimeType(FileExtension.PDF.getMimeType())
                .path(path)
                .build();

        problem.addFile(file1);
        problem.addFile(file2);//파일 추가

        fileRepository.saveAll(List.of(file1,file2));

        //문제 수정 dto 생성
        String updatedTitle = "수정된 문제";
        String updatedContent = "수정된 문제 설명";
        int updatedProblemOrder = 2;
        Section updatedSection = Section.HIGH_NORMAL;

        UpdateProblemDto updateProblemDto = UpdateProblemDto.builder()
                .problemId(savedProblem.getId())
                .contestId(savedContest.getId())
                .title(updatedTitle)
                .content(updatedContent)
                .problemOrder(updatedProblemOrder)
                .section(updatedSection)
                .deleteFileIds(List.of(file1.getId())) //첫 번째 파일만 삭제
                .build();

        //새 파일 추가
        String newFilename = "새파일.pdf";
        FileSource newFileSource = new FileSource(
                "new_upload.pdf",
                newFilename,
                new byte[]{7, 8, 9},
                "application/pdf",
                FileExtension.PDF,
                300L
        );
        String expectedPath = "/contest/16회/고등-일반/2번";
        FileSources fileSources = FileSources.of(List.of(newFileSource));

        //when
        contestProblemAdminService.updateContestProblem(updateProblemDto, fileSources);
        //then
        //문제 정보가 업데이트되었는지 확인
        entityManager.flush();
        entityManager.clear();

        Problem updatedProblem = problemRepository.findById(savedProblem.getId()).orElseThrow();
        assertThat(updatedProblem).extracting("title", "content", "problemOrder", "section")
                .containsExactly(updatedTitle, updatedContent, updatedProblemOrder, updatedSection);

        //파일이 적절히 삭제되었는지 확인
        List<File> remainingFiles = fileRepository.findAll();
        assertThat(remainingFiles).hasSize(2) //기존 파일 1개 제거, 새 파일 1개 추가로 총 2개
                .extracting("originalName")
                .containsExactlyInAnyOrder(originalFilename2, newFilename); //첫 번째 파일은 삭제됨

    }

    @DisplayName("파일 삭제만 수행하는 경우")
    @Test
    void updateContestProblemWithOnlyDeleteFiles() {
        //given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .season(16)
                .title("16회 창의력 경진대회")
                .description("16회 창의력 경진대회 설명")
                .registrationStartAt(now.plusDays(1))
                .registrationEndAt(now.plusDays(2))
                .startTime(now.plusDays(3))
                .endTime(now.plusDays(4))
                .build();

        Contest savedContest = contestRepository.save(contest);

        Problem problem = Problem.builder()
                .title("기존 문제")
                .content("기존 문제 설명")
                .problemOrder(1)
                .section(Section.COMMON)
                .problemType(ProblemType.CONTEST) // 추가
                .contest(savedContest)
                .build();

        Problem savedProblem = problemRepository.save(problem);

        String path = "/contest/16회/공통/1번";

        //두 개의 파일 생성
        File file1 = File.builder()
                .originalName("파일1.pdf")
                .name("file1.pdf")
                .extension(FileExtension.PDF)
                .size(100L)
                .fileType(FileType.PROBLEM_REAL) // 추가
                .mimeType(FileExtension.PDF.getMimeType()) // 추가
                .path(path) // 추가
                .problem(savedProblem)
                .build();

        File file2 = File.builder()
                .originalName("파일2.pdf")
                .name("file2.pdf")
                .extension(FileExtension.PDF)
                .size(200L)
                .fileType(FileType.PROBLEM_REAL) // 추가
                .mimeType(FileExtension.PDF.getMimeType()) // 추가
                .path(path) // 추가
                .problem(savedProblem)
                .build();

        problem.addFile(file1);
        problem.addFile(file2);

        fileRepository.saveAll(List.of(file1, file2));

        //두 파일 모두 삭제하는 DTO 생성
        UpdateProblemDto updateProblemDto = UpdateProblemDto.builder()
                .problemId(savedProblem.getId())
                .contestId(savedContest.getId())
                .title("기존 문제") // 제목 변경 없음
                .content("기존 문제 설명") // 내용 변경 없음
                .problemOrder(1) // 순서 변경 없음
                .section(Section.COMMON) // 섹션 변경 없음
                .deleteFileIds(List.of(file1.getId(), file2.getId())) // 모든 파일 삭제
                .build();

        //빈 파일 소스 (새 파일 없음)
        FileSources emptyFileSources = FileSources.of(Collections.emptyList());

        String expectedPath = "/contest/16회/공통/1번";

        //when
        contestProblemAdminService.updateContestProblem(updateProblemDto, emptyFileSources);
        entityManager.flush();
        entityManager.clear();

        //then
        List<File> remainingFiles = fileRepository.findAll();
        assertThat(remainingFiles).isEmpty(); // 모든 파일이 삭제되었는지 확인
    }

    @DisplayName("파일 추가만 수행하는 경우")
    @Test
    void updateContestProblemWithOnlyAddFiles() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .season(16)
                .title("16회 창의력 경진대회")
                .description("16회 창의력 경진대회 설명")
                .registrationStartAt(now.plusDays(1))
                .registrationEndAt(now.plusDays(2))
                .startTime(now.plusDays(3))
                .endTime(now.plusDays(4))
                .build();

        Contest savedContest = contestRepository.save(contest);

        Problem problem = Problem.builder()
                .title("기존 문제")
                .content("기존 문제 설명")
                .problemOrder(1)
                .section(Section.COMMON)
                .problemType(ProblemType.CONTEST) // 추가
                .contest(savedContest)
                .build();

        Problem savedProblem = problemRepository.save(problem);

        // 파일 없는 상태에서 시작

        // 문제 정보는 변경하지 않고 파일만 추가하는 DTO
        UpdateProblemDto updateProblemDto = UpdateProblemDto.builder()
                .problemId(savedProblem.getId())
                .contestId(savedContest.getId())
                .title("기존 문제")
                .content("기존 문제 설명")
                .problemOrder(1)
                .section(Section.COMMON)
                .deleteFileIds(Collections.emptyList()) // 삭제할 파일 없음
                .build();

        // 새 파일 2개 추가
        FileSource newFileSource1 = new FileSource(
                "new1.pdf",
                "새파일1.pdf",
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        FileSource newFileSource2 = new FileSource(
                "new2.pdf",
                "새파일2.pdf",
                new byte[]{4, 5, 6},
                "application/pdf",
                FileExtension.PDF,
                200L
        );

        FileSources fileSources = FileSources.of(List.of(newFileSource1, newFileSource2));

        String expectedPath = "/contest/16회/공통/1번";

        // when
        contestProblemAdminService.updateContestProblem(updateProblemDto, fileSources);
        entityManager.flush();
        entityManager.clear();

        // then
        List<File> addedFiles = fileRepository.findAll();
        assertThat(addedFiles).hasSize(2)
                .extracting("originalName")
                .containsExactlyInAnyOrder("새파일1.pdf", "새파일2.pdf");

        // 파일 세부 속성 확인
        assertThat(addedFiles).allSatisfy(file -> {
            assertThat(file.getFileType()).isEqualTo(FileType.PROBLEM_REAL);
            assertThat(file.getMimeType()).isEqualTo(FileExtension.PDF.getMimeType());
            assertThat(file.getPath()).isEqualTo(expectedPath);
        });
    }

    @DisplayName("섹션과 순서를 변경하는 경우")
    @Test
    void updateContestProblemWithChangeSectionAndOrder() {
        //given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .season(16)
                .title("16회 창의력 경진대회")
                .description("16회 창의력 경진대회 설명")
                .registrationStartAt(now.plusDays(1))
                .registrationEndAt(now.plusDays(2))
                .startTime(now.plusDays(3))
                .endTime(now.plusDays(4))
                .build();

        Contest savedContest = contestRepository.save(contest);

        //원래는 COMMON 섹션의 1번 문제
        Problem problem = Problem.builder()
                .title("기존 문제")
                .content("기존 문제 설명")
                .problemOrder(1)
                .section(Section.COMMON)
                .problemType(ProblemType.CONTEST) // 추가
                .contest(savedContest)
                .build();

        Problem savedProblem = problemRepository.save(problem);

        //기존 파일 경로
        String originalPath = "/contest/16회/공통/1번";

        //기존 파일 생성
        File file = File.builder()
                .originalName("기존파일.pdf")
                .name("existing.pdf")
                .extension(FileExtension.PDF)
                .size(100L)
                .fileType(FileType.PROBLEM_REAL) // 추가
                .mimeType(FileExtension.PDF.getMimeType()) // 추가
                .path(originalPath) // 추가
                .problem(savedProblem)
                .build();

        problem.addFile(file);
        fileRepository.save(file);

        //섹션과 순서를 변경하는 DTO
        UpdateProblemDto updateProblemDto = UpdateProblemDto.builder()
                .problemId(savedProblem.getId())
                .contestId(savedContest.getId())
                .title("기존 문제") //제목 변경 없음
                .content("기존 문제 설명") //내용 변경 없음
                .problemOrder(3) // 순서 변경: 1 -> 3
                .section(Section.ELEMENTARY_MIDDLE)
                .deleteFileIds(Collections.emptyList()) //삭제할 파일 없음
                .build();

        //새 파일 추가
        FileSource newFileSource = new FileSource(
                "new.pdf",
                "새파일.pdf",
                new byte[]{1, 2, 3},
                "application/pdf",
                FileExtension.PDF,
                100L
        );

        FileSources fileSources = FileSources.of(List.of(newFileSource));

        //경로 변경 (공통/1번 -> 초등-중등/3번)
        String expectedPath = "/contest/16회/초등-중등/3번";

        //when
        contestProblemAdminService.updateContestProblem(updateProblemDto, fileSources);

        //then
        Problem updatedProblem = problemRepository.findById(savedProblem.getId()).orElseThrow();
        assertThat(updatedProblem).extracting("section", "problemOrder", "problemType")
                .containsExactly(Section.ELEMENTARY_MIDDLE, 3, ProblemType.CONTEST); // 섹션과 순서가 변경되었는지 확인

        List<File> files = fileRepository.findAll();
        assertThat(files).hasSize(2)
                .extracting("originalName")
                .containsExactlyInAnyOrder("기존파일.pdf", "새파일.pdf");

        //새 파일은 새 경로를 가짐
        File newFile = files.stream()
                .filter(f -> f.getOriginalName().equals("새파일.pdf"))
                .findFirst()
                .orElseThrow();
        assertThat(newFile.getPath()).isEqualTo(expectedPath);
        assertThat(newFile.getFileType()).isEqualTo(FileType.PROBLEM_REAL);
        assertThat(newFile.getMimeType()).isEqualTo(FileExtension.PDF.getMimeType());

        verify(fileStorage, times(1)).upload(eq(expectedPath), any(FileSources.class));
    }

    @DisplayName("존재하지 않는 문제를 수정하려고 할 때 예외가 발생한다")
    @Test
    void updateContestProblemWithThrowsException() {
        //given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .season(16)
                .title("16회 창의력 경진대회")
                .description("16회 창의력 경진대회 설명")
                .registrationStartAt(now.plusDays(1))
                .registrationEndAt(now.plusDays(2))
                .startTime(now.plusDays(3))
                .endTime(now.plusDays(4))
                .build();

        Contest savedContest = contestRepository.save(contest);

        //존재하지 않는 문제 ID
        Long nonExistentProblemId = 9999L;

        UpdateProblemDto updateProblemDto = UpdateProblemDto.builder()
                .problemId(nonExistentProblemId)
                .contestId(savedContest.getId())
                .title("수정된 문제")
                .content("수정된 내용")
                .problemOrder(1)
                .section(Section.COMMON)
                .deleteFileIds(Collections.emptyList())
                .build();

        FileSources emptyFileSources = FileSources.of(Collections.emptyList());

        //when
        //then
        assertThatThrownBy(() -> contestProblemAdminService.updateContestProblem(updateProblemDto, emptyFileSources))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("해당 문제는 존재하지 않습니다.");
    }

    @DisplayName("문제를 삭제하면 파일도 같이 삭제된다.")
    @Test
    void deleteContestProblem(){
        //given
        LocalDateTime now = LocalDateTime.now();
        Contest contest = Contest.builder()
                .season(16)
                .title("16회 창의력 경진대회")
                .description("16회 창의력 경진대회 설명")
                .registrationStartAt(now.plusDays(1))
                .registrationEndAt(now.plusDays(2))
                .startTime(now.plusDays(3))
                .endTime(now.plusDays(4))
                .build();

        Contest savedContest = contestRepository.save(contest);

        Problem problem = Problem.builder()
                .title("기존 문제")
                .content("기존 문제 설명")
                .problemOrder(1)
                .section(Section.COMMON)
                .problemType(ProblemType.CONTEST)
                .contest(savedContest)
                .build();

        Problem savedProblem = problemRepository.save(problem);

        String path = "/contest/16회/공통/1번";

        //두 개의 파일 생성
        File file1 = File.builder()
                .originalName("파일1.pdf")
                .name("file1.pdf")
                .extension(FileExtension.PDF)
                .size(100L)
                .fileType(FileType.PROBLEM_REAL)
                .mimeType(FileExtension.PDF.getMimeType())
                .path(path)
                .problem(savedProblem)
                .build();

        File file2 = File.builder()
                .originalName("파일2.pdf")
                .name("file2.pdf")
                .extension(FileExtension.PDF)
                .size(200L)
                .fileType(FileType.PROBLEM_REAL)
                .mimeType(FileExtension.PDF.getMimeType())
                .path(path)
                .problem(savedProblem)
                .build();

        problem.addFile(file1);
        problem.addFile(file2);
        fileRepository.saveAll(List.of(file1, file2));

        entityManager.flush();
        entityManager.clear();

        //when
        contestProblemAdminService.deleteContestProblem(problem.getId());

        //then
        List<Problem> allProblem = problemRepository.findAll();
        List<File> allFile = fileRepository.findAll();

        assertThat(allProblem).isEmpty();
        assertThat(allFile).isEmpty();
    }


}