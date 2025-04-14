package com.example.cpsplatform.problem.admin.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.storage.FileStorage;
import com.example.cpsplatform.problem.admin.service.dto.AddProblemDto;
import com.example.cpsplatform.problem.domain.Problem;
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

}