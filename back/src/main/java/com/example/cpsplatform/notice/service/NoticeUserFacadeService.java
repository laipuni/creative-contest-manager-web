package com.example.cpsplatform.notice.service;

import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.notice.controller.response.UserNoticeDetailResponse;
import com.example.cpsplatform.notice.domain.Notice;
import com.example.cpsplatform.notice.repository.NoticeRepository;
import com.example.cpsplatform.security.encoder.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeUserFacadeService {

    private final NoticeRepository noticeRepository;
    private final FileRepository fileRepository;
    private final CryptoService cryptoService;

    @Transactional
    public UserNoticeDetailResponse retrieveNotice(final Long noticeId) {
        log.debug("공지사항(id:{}) 단건 조회 요청", noticeId); // 개발/테스트에서만 의미
        noticeRepository.increaseViewCount(noticeId);
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항은 존재하지 않습니다."));
        List<File> files = fileRepository.findAllByNoticeId(noticeId);
        log.debug("공지사항의 첨부파일(id:{}) 조회",files.stream().map(File::getId).toList());
        return UserNoticeDetailResponse.of(notice,files,cryptoService);
    }
}
