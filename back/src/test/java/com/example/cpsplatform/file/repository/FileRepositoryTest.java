package com.example.cpsplatform.file.repository;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.ProblemType;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
@SpringBootTest
class FileRepositoryTest {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    ContestRepository contestRepository;

    @DisplayName("문제의 id로 삭제되지 않은 출제용 문제 파일을 복수 조회한다.")
    @Test
    void findAllByProblemIdAndFileTypeAndNotDeleted(){
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

        List<File> files = List.of(
                createFile(problem,"문제1_1.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/1번"),
                createFile(problem,"문제1_2.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/2번"),
                createFile(problem,"문제1_3.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/3번"),
                createFile(problem,"문제1_4.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/4번")
        );

        fileRepository.saveAll(files);

        //when
        List<File> result = fileRepository.findAllByProblemIdAndFileTypeAndNotDeleted(problem.getId(), FileType.PROBLEM_REAL);
        //then

        assertThat(result).hasSize(4)
                .extracting("problem","name","originalName","fileType","mimeType","extension","size","path","deleted")
                .containsExactly(
                        tuple(problem,"문제1_1.pdf","문제1_1.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100L, "/contest/16회/일반/1번",false),
                        tuple(problem,"문제1_2.pdf","문제1_2.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100L, "/contest/16회/일반/2번",false),
                        tuple(problem,"문제1_3.pdf","문제1_3.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100L, "/contest/16회/일반/3번",false),
                        tuple(problem,"문제1_4.pdf","문제1_4.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100L, "/contest/16회/일반/4번",false)
                );
    }

    @DisplayName("문제의 id로 문제 파일을 soft delete 한다.")
    @Test
    void softDeletedByProblemId(){
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

        List<File> files = List.of(
                createFile(problem,"문제1_1.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/1번"),
                createFile(problem,"문제1_2.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/2번"),
                createFile(problem,"문제1_3.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/3번"),
                createFile(problem,"문제1_4.pdf", FileType.PROBLEM_REAL, FileExtension.PDF.getMimeType(), FileExtension.PDF, 100, "/contest/16회/일반/4번")
        );

        fileRepository.softDeletedByProblemId(problem.getId());

        //when
        List<File> result = fileRepository.findAllByProblemIdAndFileTypeAndNotDeleted(problem.getId(), FileType.PROBLEM_REAL);
        //then
        assertThat(result).isEmpty();
    }




    private static File createFile(Problem problem,String name, FileType fileType, String mimeType, FileExtension extension, long size, String path) {
        return File.builder()
                .problem(problem)
                .name(name)
                .originalName(name)
                .fileType(fileType)
                .mimeType(mimeType)
                .extension(extension)
                .size(size)
                .path(path)
                .build();
    }

}