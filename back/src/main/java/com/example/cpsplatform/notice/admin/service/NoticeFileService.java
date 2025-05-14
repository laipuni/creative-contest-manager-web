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
