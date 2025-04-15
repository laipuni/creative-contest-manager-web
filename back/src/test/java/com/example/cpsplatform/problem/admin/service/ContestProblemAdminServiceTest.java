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
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        String expectedPath = "/contest/16회/공통/3번/";

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
}