package com.example.cpsplatform.notice.service;

import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.notice.domain.Notice;
import com.example.cpsplatform.notice.repository.NoticeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NoticeUserFacadeServiceTest {

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    NoticeUserFacadeService noticeUserFacadeService;

    @Autowired
    MemberRepository memberRepository;

    Notice notice;
    File file;
    @BeforeEach
    @Transactional
    void tearUp(){
        String loginId = "admin";
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        String phoneNumber = "010" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);
        Member admin = Member.builder()
                .loginId(loginId)
                .password("1234")
                .role(Role.ADMIN)
                .birth(LocalDate.now())
                .email(loginId + "@email.com")
                .address(address)
                .gender(Gender.MAN)
                .phoneNumber(phoneNumber)
                .name("리더")
                .organization(school)
                .build();
        memberRepository.save(admin);

        notice = Notice.builder()
                .viewCount(0L)
                .writer(admin)
                .title("공지사항")
                .content("공지사항 내용")
                .build();
        noticeRepository.save(notice);

        //삭제할 공지사항 첨부파일
        file = File.builder()
                .name("삭제할_파일1.pdf")
                .originalName("삭제할_파일1.pdf")
                .extension(FileExtension.PDF)
                .mimeType(FileExtension.PDF.getMimeType())
                .size(100L)
                .path("/notice/" + FileExtension.PDF.getExtension())
                .fileType(FileType.NOTICE)
                .notice(notice)
                .build();

        fileRepository.save(file);
    }

    @AfterEach
    void tearDown(){
        fileRepository.hardDeleteAllByIdIn(List.of(file.getId()));
        noticeRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("쓰레드 100개로 100회 조회 요청을 했을 때, 조회수 100만큼 올라간다")
    @Test
    void retrieveNotice() throws InterruptedException {
        //given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    noticeUserFacadeService.retrieveNotice(notice.getId());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        //then
        Notice result = noticeRepository.findById(notice.getId()).get();
        assertThat(result.getViewCount()).isEqualTo(100L);
    }

}