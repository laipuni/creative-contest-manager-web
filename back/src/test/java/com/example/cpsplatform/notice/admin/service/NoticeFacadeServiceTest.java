package com.example.cpsplatform.notice.admin.service;

import com.example.cpsplatform.file.decoder.vo.FileSource;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.file.domain.FileExtension;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.file.storage.FileStorage;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.notice.admin.controller.response.NoticeAddResponse;
import com.example.cpsplatform.notice.domain.Notice;
import com.example.cpsplatform.notice.repository.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

@Transactional
@SpringBootTest
class NoticeFacadeServiceTest {

    @Autowired
    NoticeFacadeService noticeFacadeService;

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    FileRepository fileRepository;

    @MockitoBean
    FileStorage fileStorage;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("공지사항 제목, 내용과 첨부할 파일을 업로드한다.")
    @Test
    void publishNotice(){
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        FileSource fileSource1 = new FileSource(
                "file1.pdf",
                "file1.pdf",
                new byte[100],
                FileExtension.PDF.getMimeType(),
                FileExtension.PDF,
                100L
        );

        FileSource fileSource2 = new FileSource(
                "file2.pdf",
                "file2.pdf",
                new byte[100],
                FileExtension.PDF.getMimeType(),
                FileExtension.PDF,
                100L
        );

        FileSources fileSources = new FileSources(List.of(fileSource1,fileSource2));

        String title = "공지사항";
        String content = "안녕하세요.";

        //when
        NoticeAddResponse result = noticeFacadeService.publishNotice(title, content, admin.getLoginId(), fileSources);
        //then
        List<Notice> notices = noticeRepository.findAll();
        List<File> files = fileRepository.findAll();

        assertThat(result).isNotNull()
                .extracting("isSuccess","message")
                .containsExactly(true,"공지사항 등록에 성공적으로 등록되었습니다.");

        assertThat(notices).hasSize(1)
                .extracting("title","content")
                .containsExactlyInAnyOrder(tuple(title,content));

        assertThat(files).hasSize(2)
                .extracting("name","originalName","extension","mimeType","size","fileType")
                .containsExactlyInAnyOrder(
                        tuple(fileSource1.getUploadFileName(),
                                fileSource1.getOriginalFilename(),
                                fileSource1.getExtension(),
                                fileSource1.getMimeType(),
                                fileSource1.getSize(),
                                FileType.NOTICE
                        ),tuple(fileSource2.getUploadFileName(),
                                fileSource2.getOriginalFilename(),
                                fileSource2.getExtension(),
                                fileSource2.getMimeType(),
                                fileSource2.getSize(),
                                FileType.NOTICE
                        )
                );
    }

    @DisplayName("공지사항 제목, 내용과 첨부할 파일없이 업로드한다.")
    @Test
    void publishNoticeWithNotNoticeFile(){
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        FileSources fileSources = new FileSources(Collections.emptyList());

        String title = "공지사항";
        String content = "안녕하세요.";

        //when
        NoticeAddResponse result = noticeFacadeService.publishNotice(title, content, admin.getLoginId(), fileSources);
        //then
        List<Notice> notices = noticeRepository.findAll();

        assertThat(result).isNotNull()
                .extracting("isSuccess","message")
                .containsExactly(true,"공지사항 등록에 성공적으로 등록되었습니다.");
        assertThat(notices).hasSize(1)
                .extracting("title","content")
                .containsExactlyInAnyOrder(tuple(title,content));
    }

    @DisplayName("공지사항을 등록할 때, 파일 업로드를 실패할 경우 공지사항을 등록하고 실패한 파일을 반환한다.")
    @Test
    void publishNoticeWith(){
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        FileSource fileSource1 = new FileSource(
                "file1.pdf",
                "file1.pdf",
                new byte[100],
                FileExtension.PDF.getMimeType(),
                FileExtension.PDF,
                100L
        );

        FileSource fileSource2 = new FileSource(
                "file2.pdf",
                "file2.pdf",
                new byte[100],
                FileExtension.PDF.getMimeType(),
                FileExtension.PDF,
                100L
        );

        FileSources fileSources = new FileSources(List.of(fileSource1,fileSource2));

        //file2.pdf를 업로드할때 예외 발생하도록 mock처리
        doAnswer(invocation -> {
            FileSource source = invocation.getArgument(1);
            if (fileSource2.getOriginalFilename().equals(source.getOriginalFilename())) {
                //두번째 파일은 실패하도록 mock
                throw new RuntimeException("업로드 실패");
            }
            return null;
        }).when(fileStorage).upload(anyString(), any(FileSource.class));


        String title = "공지사항";
        String content = "안녕하세요.";

        //when
        NoticeAddResponse result = noticeFacadeService.publishNotice(title, content, admin.getLoginId(), fileSources);
        //then
        List<Notice> notices = noticeRepository.findAll();
        List<File> files = fileRepository.findAll();

        assertThat(result).isNotNull()
                .extracting("isSuccess","message")
                .containsExactlyInAnyOrder(true, "공지사항은 등록되었지만, 첨부한 2개의 파일 중 1개는 업로드에 실패했습니다. (실패 파일: file2.pdf)");

        assertThat(notices).hasSize(1)
                .extracting("title","content")
                .containsExactlyInAnyOrder(tuple(title,content));

        assertThat(files).hasSize(1)
                .extracting("name","originalName","extension","mimeType","size","fileType")
                .containsExactlyInAnyOrder(
                        tuple(fileSource1.getUploadFileName(),
                                fileSource1.getOriginalFilename(),
                                fileSource1.getExtension(),
                                fileSource1.getMimeType(),
                                fileSource1.getSize(),
                                FileType.NOTICE
                        )
                );
    }

    private Member createAndSaveAdmin(String loginId) {
        Address address = new Address("street", "city", "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
        String phoneNumber = "010" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);
        Member member = Member.builder()
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
        return memberRepository.save(member);
    }

}