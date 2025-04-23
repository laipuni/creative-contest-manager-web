package com.example.cpsplatform.file.service.download.generator;

import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.repository.dto.FileNameDto;
import com.example.cpsplatform.problem.domain.Section;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static com.example.cpsplatform.file.service.download.generator.TeamSolveFileNameGenerator.TEAM_SOLVE_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TeamSolveFileNameGeneratorTest {

    @MockitoBean
    FileRepository fileRepository;

    @DisplayName("다운로드할 팀들의 답안지의 파일 이름을 x회_섹션_xx팀_x번_답안지.pdf와 같이 생성한다.")
    @Test
    void generate(){
        //given
        List<Long> fileIds = List.of(1L,2L,3L);

        int season = 16;
        FileExtension pdf = FileExtension.PDF;
        int problemOrder = 1;
        Section common = Section.COMMON;
        Section highNormal = Section.HIGH_NORMAL;
        Section elementaryMiddle = Section.ELEMENTARY_MIDDLE;
        String teamName1 = "xx팀";
        String teamName2 = "xxx팀";
        String teamName3 = "xxxx팀";

        List<FileNameDto> fileNameDtos = List.of(
                new FileNameDto(fileIds.get(0), pdf, common, teamName1, season, problemOrder),
                new FileNameDto(fileIds.get(problemOrder), pdf, highNormal, teamName2, season, problemOrder),
                new FileNameDto(fileIds.get(2), pdf, elementaryMiddle, teamName3, season, problemOrder)
        );

        TeamSolveFileNameGenerator teamSolveFileNameGenerator = new TeamSolveFileNameGenerator(fileRepository);
        Mockito.when(fileRepository.findFileNameDto(fileIds)).thenReturn(fileNameDtos);


        //when
        Map<Long, String> result = teamSolveFileNameGenerator.generate(fileIds);

        //then
        assertThat(result.get(fileIds.get(0))).isEqualTo(
                String.format(TEAM_SOLVE_FORMAT,
                        season,
                        common.getLabel(),
                        teamName1,
                        problemOrder,
                        pdf.getExtension()
                )
        );
        assertThat(result.get(fileIds.get(1))).isEqualTo(
                String.format(TEAM_SOLVE_FORMAT,
                        season,
                        highNormal.getLabel(),
                        teamName2,
                        problemOrder,
                        pdf.getExtension()
                )
        );
        assertThat(result.get(fileIds.get(2))).isEqualTo(
                String.format(TEAM_SOLVE_FORMAT,
                        season,
                        elementaryMiddle.getLabel(),
                        teamName3,
                        problemOrder,
                        pdf.getExtension()
                )
        );
    }


}