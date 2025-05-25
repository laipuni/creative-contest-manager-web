package com.example.cpsplatform.file.admin;

import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.service.FileService;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.teamsolve.domain.TeamSolveType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.cpsplatform.file.cofig.FileConfig.TEAM_SOLVE_ZIP_DOWNLOAD;

@RestController
@RequiredArgsConstructor
public class FileAdminController {

    private final FileDownloadService fileDownloadService;
    private final FileService fileService;

    @AdminLog
    @GetMapping("/api/admin/v1/contests/{contestId}/answers/zip-download")
    public void testZipDownload(@PathVariable("contestId") Long contestId,
                                @RequestParam(value = "zipName",defaultValue = "answers") String zipName,
                                HttpServletResponse response){
        //팀들 답안지 파일 id들 가져오기
        List<Long> fileIds = fileService.getTeamSolveFileIdsByContestId(contestId, TeamSolveType.SUBMITTED);
        fileDownloadService.downloadAsZip(fileIds, response, zipName, TEAM_SOLVE_ZIP_DOWNLOAD);
    }

    @AdminLog
    @GetMapping("/api/admin/files/{fileId}")
    public void testZipDownload(@PathVariable("fileId") Long fileId,
                                HttpServletResponse response){
        fileDownloadService.download(fileId, response);
    }

}
