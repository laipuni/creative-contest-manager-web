package com.example.cpsplatform.notice.admin.service;

import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.storage.FileStorage;
import com.example.cpsplatform.notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeFileService {

    public static final String NOTICE_FILE_UPLOAD_LOG = "[NOTICE_FILE_SERVICE]";

    private final FileStorage fileStorage;
    private final FileRepository fileRepository;

    /**
     * 공지사항 파일을 올리는 메서드
     * @param notice : 파일을 올릴 공지사항
     * @param fileSources : 파일의 메타데이터들
     * @return 실패한 파일 이름들
     */
    @Transactional
    public List<String> uploadNoticeFile(final Notice notice, final FileSources fileSources) {
        List<String> failedFiles = new ArrayList<>();
        List<File> files = new ArrayList<>();

        for (FileSource fileSource : fileSources.getFileSourceList()) {
            String path = getNoticeFilePath(fileSource);
            try {
                fileStorage.upload(path, fileSource);
                File file = createNoticeFile(notice, fileSource, path);
                files.add(file);
            } catch (Exception e) {
                log.error("{} 공지사항 첨부파일 업로드 실패, 파일명: {}, 에러: {}",NOTICE_FILE_UPLOAD_LOG, fileSource.getOriginalFilename(), e.getMessage());
                failedFiles.add(fileSource.getOriginalFilename());
            }
        }

        fileRepository.saveAll(files);
        return failedFiles;
    }

    @Transactional
    public List<String> deleteNoticeFiles(final List<Long> deleteFileIds) {
        if (deleteFileIds == null || deleteFileIds.isEmpty()) {
            log.info("삭제할 공지사항 첨부 파일이 없습니다.");
            return Collections.emptyList();
        }
        List<File> files = fileRepository.findAllById(deleteFileIds);
        if (files.isEmpty()) {
            log.warn("삭제 요청된 파일({}) 에 해당하는 파일을 찾을 수 없습니다.", deleteFileIds);
            return Collections.emptyList();
        }

        log.info("{} 공지사항 첨부 파일({})들을 외부스토리지에서 삭제합니다.", NOTICE_FILE_UPLOAD_LOG, deleteFileIds);

        //외부 스토리지 삭제
        List<Long> successfullyDeletedIds = new ArrayList<>();
        List<String> deleteFailedFilenames = new ArrayList<>();
        for (File file : files) {
            try {
                fileStorage.delete(file.getPath(), file.getName());
                successfullyDeletedIds.add(file.getId());
            } catch (Exception e) {
                log.error("파일 스토리지에서 파일 삭제 실패: {}, 오류: {}", file.getId(), e.getMessage(), e);
                deleteFailedFilenames.add(file.getOriginalName());
            }
        }

        //성공적으로 외부 스토리지에 삭제된 파일들의 메타데이터만 삭제
        if (!successfullyDeletedIds.isEmpty()) {
            fileRepository.hardDeleteAllByIdIn(successfullyDeletedIds);
            log.info("공지사항 첨부 파일 {}개를 DB에서 성공적으로 삭제했습니다.", successfullyDeletedIds.size());
        }
        return deleteFailedFilenames;
    }


    private static File createNoticeFile(final Notice notice, final FileSource fileSource, final String path) {
        return File.createNoticeFile(
                fileSource.getUploadFileName(),
                fileSource.getOriginalFilename(),
                fileSource.getExtension(),
                fileSource.getMimeType(),
                fileSource.getSize(),
                path,
                notice
        );
    }

    private static String getNoticeFilePath(final FileSource fileSource) {
        //ex) "/notice/pdf"
        return "/notice/" + fileSource.getExtension().getExtension();
    }
}
