package com.example.cpsplatform.notice.admin.service;

import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.storage.FileStorage;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.notice.domain.Notice;
import com.example.cpsplatform.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeAdminService {

    public static final String NOTICE_ADMIN_LOG = "[NOTICE_ADMIN_SERVICE]";

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    @Transactional
    public Notice save(final String title, final String content, final String username) {
        log.info("{} 관리자({})님이 \"{}\" 공지사항을 생성합니다.",
                NOTICE_ADMIN_LOG, username, title);
        Member member = memberRepository.findMemberByLoginId(username)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
        return noticeRepository.save(Notice.of(title,content,member));
    }

    @Transactional
    public Notice modify(final Long noticeId, final String title, final String content, final String username) {
        log.info("{} 관리자({})님이 \"{}\" 공지사항을 수정합니다.",
                NOTICE_ADMIN_LOG, username, title);
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 공지사항이 존재하지 않습니다."));
        notice.modify(title,content);
        return notice;
    }

    @Transactional
    public void deleteNotice(final Long noticeId) {
        log.info("{} 공지사항(id:{})을 삭제합니다.",NOTICE_ADMIN_LOG,noticeId);
        noticeRepository.deleteById(noticeId);
    }
}
