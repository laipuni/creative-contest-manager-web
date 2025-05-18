package com.example.cpsplatform.notice.repository;

import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.notice.admin.controller.response.NoticeSearchDto;
import com.example.cpsplatform.notice.admin.controller.response.NoticeSearchResponse;
import com.example.cpsplatform.notice.controller.response.UserNoticeSearchDto;
import com.example.cpsplatform.notice.controller.response.UserNoticeSearchResponse;
import com.example.cpsplatform.notice.domain.Notice;
import com.example.cpsplatform.notice.repository.dto.AdminSearchNoticeCond;
import com.example.cpsplatform.notice.repository.dto.UserSearchNoticeCond;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class NoticeRepositoryCustomImplTest {

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("제목으로 검색했을 때, 해당 키워드를 포함한 공지만 조회된다")
    void searchNoticeByAdminCondWithTitleKeyword() {
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        noticeRepository.save(Notice.builder()
                .title("공지사항입니다")
                .content("내용1")
                .writer(admin)
                .viewCount(10L)
                .build());

        noticeRepository.save(Notice.builder()
                .title("다른 제목")
                .content("내용2")
                .writer(admin)
                .viewCount(20L)
                .build());

        AdminSearchNoticeCond cond = AdminSearchNoticeCond.builder()
                .searchType("title")
                .keyword("공지")
                .page(0)
                .pageSize(10)
                .build();

        //when
        NoticeSearchResponse response = noticeRepository.searchNoticeByAdminCond(cond);

        //then
        assertThat(response.getSize()).isEqualTo(1);
        assertThat(response.getNoticeSearchDtoList()).hasSize(1);
        assertThat(response.getNoticeSearchDtoList().get(0).getTitle()).contains("공지");
    }

    @Test
    @DisplayName("조회수 기준으로 정렬하면 내림차순으로 정렬된다")
    void searchNoticeByAdminCondWithOrderByViewCountDESC() {
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        noticeRepository.save(Notice.builder()
                .title("공지 A")
                .content("내용")
                .writer(admin)
                .viewCount(15L)
                .build());

        noticeRepository.save(Notice.builder()
                .title("공지 B")
                .content("내용")
                .writer(admin)
                .viewCount(30L)
                .build());

        AdminSearchNoticeCond cond = AdminSearchNoticeCond.builder()
                .orderType("viewCount")
                .order("desc")
                .page(0)
                .pageSize(10)
                .build();

        //when
        NoticeSearchResponse response = noticeRepository.searchNoticeByAdminCond(cond);

        //then
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(response.getNoticeSearchDtoList().get(0).getViewCount()).isGreaterThanOrEqualTo(
                response.getNoticeSearchDtoList().get(1).getViewCount()
        );
    }

    @Test
    @DisplayName("생성일 기준으로 정렬하면 오름차순으로 정렬된다")
    void searchNoticeByAdminCondWithOrderCreatedAtASC() throws InterruptedException {
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        noticeRepository.save(Notice.builder()
                .title("먼저 생성된 공지")
                .content("내용")
                .writer(admin)
                .viewCount(10L)
                .build());

        sleep(100); // 시간 차이를 주기 위해

        noticeRepository.save(Notice.builder()
                .title("나중에 생성된 공지")
                .content("내용")
                .writer(admin)
                .viewCount(10L)
                .build());

        AdminSearchNoticeCond cond = AdminSearchNoticeCond.builder()
                .orderType("createdAt")
                .order("asc")
                .page(0)
                .pageSize(10)
                .build();

        //when
        NoticeSearchResponse response = noticeRepository.searchNoticeByAdminCond(cond);

        //then
        List<NoticeSearchDto> list = response.getNoticeSearchDtoList();
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(list.get(0).getCreatedAt()).isBefore(list.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("페이지 사이즈가 1이면 한 개만 조회되며 전체 개수는 그대로 반환된다")
    void searchNoticeByAdminCondWithOnlyOnePageAndOneElement() {
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        noticeRepository.save(Notice.builder()
                .title("공지1")
                .content("내용")
                .writer(admin)
                .viewCount(10L)
                .build());

        noticeRepository.save(Notice.builder()
                .title("공지2")
                .content("내용")
                .writer(admin)
                .viewCount(20L)
                .build());

        AdminSearchNoticeCond cond = AdminSearchNoticeCond.builder()
                .page(0)
                .pageSize(1)
                .orderType("viewCount")
                .order("desc")
                .build();

        //when
        NoticeSearchResponse response = noticeRepository.searchNoticeByAdminCond(cond);

        //then
        assertThat(response.getSize()).isEqualTo(2); // 전체 개수
        assertThat(response.getNoticeSearchDtoList()).hasSize(1); // 한 페이지만
        assertThat(response.getTotalPage()).isEqualTo(2); // 페이지 수
    }

    @Test
    @DisplayName("제목으로 검색했을 때, 해당 키워드를 포함한 공지만 조회된다")
    void searchNoticeByUserCondWithTitleKeyword() {
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        noticeRepository.save(Notice.builder()
                .title("공지사항입니다")
                .content("내용1")
                .writer(admin)
                .viewCount(10L)
                .build());

        noticeRepository.save(Notice.builder()
                .title("다른 제목")
                .content("내용2")
                .writer(admin)
                .viewCount(20L)
                .build());

        UserSearchNoticeCond cond = UserSearchNoticeCond.builder()
                .searchType("title")
                .keyword("공지")
                .page(0)
                .pageSize(10)
                .build();

        //when
        UserNoticeSearchResponse response = noticeRepository.searchNoticeByUserCond(cond);

        //then
        assertThat(response.getSize()).isEqualTo(1);
        assertThat(response.getNoticeSearchDtoList()).hasSize(1);
        assertThat(response.getNoticeSearchDtoList().get(0).getTitle()).contains("공지");
    }

    @Test
    @DisplayName("조회수 기준으로 정렬하면 내림차순으로 정렬된다")
    void searchNoticeByUserCondWithOrderByViewCountDESC() {
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        noticeRepository.save(Notice.builder()
                .title("공지 A")
                .content("내용")
                .writer(admin)
                .viewCount(15L)
                .build());

        noticeRepository.save(Notice.builder()
                .title("공지 B")
                .content("내용")
                .writer(admin)
                .viewCount(30L)
                .build());

        UserSearchNoticeCond cond = UserSearchNoticeCond.builder()
                .orderType("viewCount")
                .order("desc")
                .page(0)
                .pageSize(10)
                .build();

        //when
        UserNoticeSearchResponse response = noticeRepository.searchNoticeByUserCond(cond);

        //then
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(response.getNoticeSearchDtoList().get(0).getViewCount()).isGreaterThanOrEqualTo(
                response.getNoticeSearchDtoList().get(1).getViewCount()
        );
    }

    @Test
    @DisplayName("생성일 기준으로 정렬하면 오름차순으로 정렬된다")
    void searchNoticeByUserCondWithOrderCreatedAtASC() throws InterruptedException {
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        noticeRepository.save(Notice.builder()
                .title("먼저 생성된 공지")
                .content("내용")
                .writer(admin)
                .viewCount(10L)
                .build());

        sleep(100); // 시간 차이를 주기 위해

        noticeRepository.save(Notice.builder()
                .title("나중에 생성된 공지")
                .content("내용")
                .writer(admin)
                .viewCount(10L)
                .build());

        UserSearchNoticeCond cond = UserSearchNoticeCond.builder()
                .orderType("createdAt")
                .order("asc")
                .page(0)
                .pageSize(10)
                .build();

        //when
        UserNoticeSearchResponse response = noticeRepository.searchNoticeByUserCond(cond);

        //then
        List<UserNoticeSearchDto> list = response.getNoticeSearchDtoList();
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(list.get(0).getCreatedAt()).isBefore(list.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("페이지 사이즈가 1이면 한 개만 조회되며 전체 개수는 그대로 반환된다")
    void searchNoticeByUserCondWithOnlyOnePageAndOneElement() {
        //given
        Member admin = createAndSaveAdmin("adminLoginId");

        noticeRepository.save(Notice.builder()
                .title("공지1")
                .content("내용")
                .writer(admin)
                .viewCount(10L)
                .build());

        noticeRepository.save(Notice.builder()
                .title("공지2")
                .content("내용")
                .writer(admin)
                .viewCount(20L)
                .build());

        UserSearchNoticeCond cond = UserSearchNoticeCond.builder()
                .page(0)
                .pageSize(1)
                .orderType("viewCount")
                .order("desc")
                .build();

        //when
        UserNoticeSearchResponse response = noticeRepository.searchNoticeByUserCond(cond);

        //then
        assertThat(response.getSize()).isEqualTo(2); // 전체 개수
        assertThat(response.getNoticeSearchDtoList()).hasSize(1); // 한 페이지만
        assertThat(response.getTotalPage()).isEqualTo(2); // 페이지 수
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