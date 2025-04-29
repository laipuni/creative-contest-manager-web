package com.example.cpsplatform;

import com.example.cpsplatform.ai.service.AiApiService;
import com.example.cpsplatform.file.cofig.FileConfig;
import com.example.cpsplatform.file.service.download.FileDownloadService;
import com.example.cpsplatform.file.service.download.dto.FileDownLoadResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.cpsplatform.file.cofig.FileConfig.TEAM_SOLVE_ZIP_DOWNLOAD;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final AiApiService apiService;
    private final FileDownloadService fileDownloadService;

    @GetMapping("/api/test")
    public ApiResponse<Object> testGet(@RequestParam(value = "request",defaultValue = "") String request){
        log.info("it's just a get test, request = {}", request);
//        String body = apiService.getTest(request);
//        log.info("receive Ai server Response response = {} ", body);
        return ApiResponse.ok(null);
    }

    @PostMapping("/api/test")
    public ApiResponse<Object> testPost(@Valid @RequestBody TestRequest request){
        log.info("it's just a post test, request = {}", request);
//        TestResponse body = apiService.postTest(request);
//        log.info("receive Ai server Response response = {} ", body);
        return ApiResponse.ok(null);
    }

    @GetMapping("/api/test/download")
    public void testDownload(@RequestParam("fileId") Long fileId, HttpServletResponse response){
        fileDownloadService.download(fileId,response);
    }

    @GetMapping("/api/test/zip-download")
    public void testZipDownload(@RequestParam("fileIds") List<Long> fileIds,
                                @RequestParam(value = "zipName",defaultValue = "zip") String zipName,
                                HttpServletResponse response){
        fileDownloadService.downloadAsZip(fileIds, response, zipName, TEAM_SOLVE_ZIP_DOWNLOAD);
    }
}
