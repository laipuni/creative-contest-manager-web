package com.example.cpsplatform.queue.job;

import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerSubmitJob {

    private Long teamId;
    private List<Long> problemIdList;
    private Map<Long, String> pathMap; // (문제 id, 경로 id)
    private Map<Long, FileSource> problemFileMap; // (문제 id, 파일 메타데이터)

    public static AnswerSubmitJob of(Long teamId, List<Long> problemIdList, List<FileSource> fileSources, List<String> paths){
        Map<Long,FileSource> problemFileMap = new HashMap<>();
        Map<Long,String> pathMap = new HashMap<>();
        for (int i = 0; i < problemIdList.size(); i++) {
            problemFileMap.put(problemIdList.get(i),fileSources.get(i));
            pathMap.put(problemIdList.get(i), paths.get(i));
        }
        return AnswerSubmitJob.builder()
                .teamId(teamId)
                .problemIdList(problemIdList)
                .problemFileMap(problemFileMap)
                .pathMap(pathMap)
                .build();
    }
    public String getPath(Long idx){
        return pathMap.get(idx);
    }

    public FileSource getFileSource(Long idx){
        return problemFileMap.get(idx);
    }

}
