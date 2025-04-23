package com.example.cpsplatform.file.service.download.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class FileDownLoadResult {

    private boolean isExistDownloadFail;
    private List<Long> downloadFailFileIds;
    private List<String> downloadFailFileNames;

    public static FileDownLoadResult success(){
        return FileDownLoadResult.builder()
                .isExistDownloadFail(false)
                .downloadFailFileIds(Collections.emptyList())
                .downloadFailFileNames(Collections.emptyList())
                .build();
    }

    public static FileDownLoadResult fail(List<Long> downloadFailFileIds, List<String> downloadFailFileNames){
        return FileDownLoadResult.builder()
                .isExistDownloadFail(true)
                .downloadFailFileIds(Collections.emptyList())
                .downloadFailFileNames(Collections.emptyList())
                .build();
    }

    public boolean isFail(){
        return isExistDownloadFail;
    }
}
