package com.example.cpsplatform.notice.admin.service;

import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.notice.admin.controller.response.NoticeAddResponse;
import com.example.cpsplatform.notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        String message = "공지사항 등록에 성공적으로 등록되었습니다.";
        List<String> failedFileNames = new ArrayList<>();
        if (fileSources.getSize() > 0) {
            failedFileNames = noticeFileService.uploadNoticeFile(notice, fileSources);
        }

        if (!failedFileNames.isEmpty()){
            //실패한 파일이 존재할 경우
            message = createFailedUploadFileMessage(failedFileNames, fileSources.getSize(),false);
        }

        return NoticeAddResponse.of(true,message, notice.getId());
    }

    private String createFailedUploadFileMessage(final List<String> failedFileNames, final int totalCount, final boolean isUpdate) {
        //등록이냐 혹은 수정이냐에 따라 메세지를 변경, 추후 메세지가 다양해질 경우 메서드 분리를 고려 중
        String action = isUpdate ? "수정" : "등록";
        StringJoiner joiner = new StringJoiner(", ");
        failedFileNames.forEach(joiner::add);

        return String.format("공지사항은 %s되었지만, 첨부한 %d개의 파일 중 %d개는 업로드에 실패했습니다. (실패 파일: %s)",
                action, totalCount, failedFileNames.size(), joiner);
    }



}
