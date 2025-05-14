package com.example.cpsplatform.notice.admin.service;

import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.notice.admin.controller.response.NoticeAddResponse;
import com.example.cpsplatform.notice.admin.controller.response.NoticeModifyResponse;
import com.example.cpsplatform.notice.admin.service.dto.NoticeModifyDto;
import com.example.cpsplatform.notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeFacadeService {

    private final NoticeAdminService noticeAdminService;
    private final NoticeFileService noticeFileService;

    @Transactional
    public NoticeAddResponse publishNotice(final String title, final String content, final String username, final FileSources fileSources){
        Notice notice = noticeAdminService.save(title, content, username);
        String message = "공지사항을 성공적으로 등록했습니다.";
        List<String> failedFileNames = new ArrayList<>();
        if (fileSources.getSize() > 0) {
            //업로드할 파일이 있는 경우
            failedFileNames = noticeFileService.uploadNoticeFile(notice, fileSources);
        }

        if (!failedFileNames.isEmpty()){
            //실패한 파일이 존재할 경우
            message = createFinalMessage(failedFileNames, Collections.emptyList(), fileSources.getSize(),0, false);
        }

        return NoticeAddResponse.of(true,message, notice.getId());
    }

    @Modifying
    public NoticeModifyResponse modifyNotice(final NoticeModifyDto noticeModifyDto, final FileSources fileSources) {
        Notice notice = noticeAdminService.modify(noticeModifyDto.getNoticeId(),
                noticeModifyDto.getTitle(),
                noticeModifyDto.getContent(),
                noticeModifyDto.getUsername()
        );

        List<String> uploadFailedFileNames = new ArrayList<>();
        List<String> deleteFailedFileNames = new ArrayList<>();

        if (fileSources.getSize() > 0) {
            // 업로드할 파일이 있는 경우
            uploadFailedFileNames = noticeFileService.uploadNoticeFile(notice, fileSources);
        }

        if (!noticeModifyDto.getDeleteFileIds().isEmpty()) {
            // 삭제할 파일이 있는 경우
            deleteFailedFileNames = noticeFileService.deleteNoticeFiles(noticeModifyDto.getDeleteFileIds());
        }

        //최종 메시지 생성
        String message = createFinalMessage(
                uploadFailedFileNames,
                deleteFailedFileNames,
                fileSources.getSize(),
                noticeModifyDto.getDeleteFileIds().size(),
                true
        );

        return NoticeModifyResponse.of(true, message, notice.getId());
    }

    private String createFinalMessage(
            final List<String> uploadFailedFileNames,
            final List<String> deleteFailedFileNames,
            final int totalUploadCount,
            final int totalDeleteCount,
            final boolean isUpdate
            ) {
        String action = isUpdate ? "수정" : "등록";

        //모두 성공한 경우
        if (uploadFailedFileNames.isEmpty() && deleteFailedFileNames.isEmpty()) {
            return String.format("공지사항을 성공적으로 %s했습니다.",action);
        }

        String base = String.format("공지사항은 %s되었지만, ", action);
        StringBuilder messageBuilder = new StringBuilder(base);
        boolean hasUploadFailed = !uploadFailedFileNames.isEmpty();
        boolean hasDeleteFailed = !deleteFailedFileNames.isEmpty();

        //업로드 실패 메시지 생성
        if (hasUploadFailed) {
            String uploadFailMsg = createUploadFailMessage(uploadFailedFileNames, totalUploadCount);
            messageBuilder.append(uploadFailMsg);
        }

        //두 메시지 사이 joiner로 연결, ~~입니다, 그리고 ~~입니다.
        if (hasUploadFailed && hasDeleteFailed) {
            messageBuilder.append(", 그리고 ");
        }

        //삭제 실패 메시지 생성
        if (hasDeleteFailed) {
            String deleteFailMsg = createDeleteFailMessage(deleteFailedFileNames, totalDeleteCount);
            messageBuilder.append(deleteFailMsg);
        }

        return messageBuilder.toString();
    }

    private String createUploadFailMessage(final List<String> uploadFailedFileNames, final int totalUploadCount) {
        StringJoiner joiner = new StringJoiner(", ");
        uploadFailedFileNames.forEach(joiner::add);

        return String.format("첨부한 %d개의 파일 중 %d개는 업로드에 실패했습니다. (실패 파일: %s)",
                totalUploadCount, uploadFailedFileNames.size(), joiner);
    }

    private String createDeleteFailMessage(final List<String> deleteFailedFileNames, final int totalDeleteCount) {
        StringJoiner joiner = new StringJoiner(", ");
        deleteFailedFileNames.forEach(joiner::add);

        return String.format("삭제 요청한 %d개의 파일 중 %d개는 삭제에 실패했습니다. (실패 파일: %s)",
                totalDeleteCount, deleteFailedFileNames.size(), joiner);
    }

}
