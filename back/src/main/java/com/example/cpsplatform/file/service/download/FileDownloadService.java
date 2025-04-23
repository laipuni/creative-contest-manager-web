package com.example.cpsplatform.file.service.download;

import com.example.cpsplatform.exception.FileDownloadException;
import com.example.cpsplatform.exception.FileNotFoundException;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.service.download.dto.FileDownLoadResult;
import com.example.cpsplatform.file.service.download.generator.DownloadFileNameGenerator;
import com.example.cpsplatform.file.storage.FileStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileDownloadService {

    private static final String DOWNLOAD_RESULT_HEADER = "X-Download-Result";
    private static final String STREAM_CONTENT_TYPE = "application/octet-stream";
    private static final String ZIP_CONTENT_TYPE = "application/zip";
    private static final String ZIP_FILE_EXTENSION = ".zip";
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"";

    private final FileStorage fileStorage;
    private final FileRepository fileRepository;
    private final Map<String, DownloadFileNameGenerator> generatorMap;

    public void download(final Long fileId, final HttpServletResponse response) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("해당 파일은 존재하지 않습니다."));

        setSingleFileHeader(response, file.getOriginalName());

        try (InputStream in = fileStorage.download(file.getPath(), file.getName());
             OutputStream out = response.getOutputStream()) {
            StreamUtils.copy(in, out);
            log.info("파일 다운로드 완료 [id={}, name={}]", file.getId(), file.getOriginalName());
        } catch (IOException | IllegalStateException e) {
            log.error("파일 다운로드 실패 [id={}, name={}]", file.getId(), file.getOriginalName(), e);
            throw new FileDownloadException("단일 파일 다운로드 실패", e);
        }
    }

    public void downloadAsZip(final List<Long> fileIds, final HttpServletResponse response,
                              final String zipFileName, final String namingType) {
        List<File> files = fileRepository.findAllById(fileIds);
        if (files.isEmpty()) {
            throw new FileNotFoundException("요청한 파일들이 존재하지 않습니다.");
        }

        setZipFileHeader(response, zipFileName);
        // 결과 미리 예측 못하니 기본 성공 헤더라도 미리 설정
        response.setHeader(DOWNLOAD_RESULT_HEADER, "success");
        //다운로드 이름 생성기 찾기
        DownloadFileNameGenerator downloadFileNameGenerator = getFileNameGenerator(namingType);
        Map<Long, String> downloadNameMap = downloadFileNameGenerator.generate(fileIds);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            FileDownLoadResult result = compressFilesToZip(files, zipOutputStream, downloadNameMap);
            handleDownloadResult(result);
        } catch (Exception e) {
            FileDownLoadResult result = FileDownLoadResult.fail(
                    fileIds,
                    files.stream().map(File::getName).toList()
            );
            handleDownloadResult(result);
        }
    }

    private FileDownLoadResult compressFilesToZip(final List<File> files, final ZipOutputStream zipOutputStream,
                                                  final Map<Long, String> downloadNameMap) throws IOException {
        List<Long> failedFileIds = new ArrayList<>();
        List<String> failedFileNames = new ArrayList<>();
        for (File file : files) {
            String downloadName = downloadNameMap.get(file.getId());
            try (InputStream stream = fileStorage.download(file.getPath(), file.getName())) {
                zipOutputStream.putNextEntry(new ZipEntry(downloadName));
                StreamUtils.copy(stream, zipOutputStream);
                zipOutputStream.closeEntry();
                log.debug("파일 압축 완료 (id={}, name={})", file.getId(), downloadName);
            } catch (IOException | RuntimeException e) {
                failedFileIds.add(file.getId());
                failedFileNames.add(downloadName);
                log.warn("파일 압축 실패 (id={}, name={})", file.getId(), downloadName, e);
            }
        }

        return failedFileIds.isEmpty()
                ? FileDownLoadResult.success()
                : FileDownLoadResult.fail(failedFileIds, failedFileNames);
    }

    private void handleDownloadResult(FileDownLoadResult result) {
        if (result.isFail()) {
            log.warn("ZIP 파일 일부 다운로드 실패 (FileIds={})", result.getDownloadFailFileIds());
            //todo 다운로드 실패 조회 api를 위해 redis나 임시로 저장해서 조회가능하도록 구현
        }
    }

    private void setSingleFileHeader(final HttpServletResponse response, final String fileName) {
        response.setContentType(STREAM_CONTENT_TYPE);
        setContentDispositionHeader(response, fileName);
    }

    private void setZipFileHeader(final HttpServletResponse response, final String zipFileName) {
        response.setContentType(ZIP_CONTENT_TYPE);
        String fullZipFileName = zipFileName + ZIP_FILE_EXTENSION;
        setContentDispositionHeader(response, fullZipFileName);
    }


    private void setContentDispositionHeader(final HttpServletResponse response, final String fileName) {
        String encodedFilename = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", String.format(CONTENT_DISPOSITION_FORMAT, encodedFilename));
    }


    private DownloadFileNameGenerator getFileNameGenerator(String namingType) {
        DownloadFileNameGenerator generator = generatorMap.get(namingType);
        if (generator == null) {
            throw new IllegalArgumentException("지원하지 않는 파일명 생성 타입입니다: " + namingType);
        }
        return generator;
    }
}