package com.example.cpsplatform.problem.admin.controller.response;

import com.example.cpsplatform.file.domain.File;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContestProblemFileDto {

    private Long fileId;
    private String originalFileName;
    private LocalDateTime createAt;

    public static ContestProblemFileDto of(File file){
        return ContestProblemFileDto.builder()
                .fileId(file.getId())
                .originalFileName(file.getOriginalName())
                .createAt(file.getCreatedAt())
                .build();
    }

}
