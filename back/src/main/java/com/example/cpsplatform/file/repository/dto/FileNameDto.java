package com.example.cpsplatform.file.repository.dto;

import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.problem.domain.Section;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileNameDto {

    private Long fileId;
    private FileExtension fileExtension;
    private Section section;
    private String teamName;
    //todo 추후에 팀 접수번호 추가 private String teamNumber
    private int season;
    private int problemOrder;

}
