package com.example.cpsplatform.member.repository;

import com.example.cpsplatform.member.admin.controller.response.MemberInfoListDto;
import com.example.cpsplatform.member.admin.controller.response.MemberInfoListResponse;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.dto.AdminMemberSearchCond;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryCustomImplTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    void setup() {
        // 테스트용 Member 데이터 3개 생성
        Address address1 = new Address("street1", "city1", "zipCode1", "detail1");
        School school1 = new School("서울대학교", StudentType.COLLEGE, 4);
        member1 = Member.builder()
                .loginId("user1")
                .password(passwordEncoder.encode("password1"))
                .role(Role.USER)
                .birth(LocalDate.of(1990, 1, 1))
                .email("user1@email.com")
                .address(address1)
                .gender(Gender.MAN)
                .phoneNumber("01011111111")
                .name("김철수")
                .organization(school1)
                .build();

        Address address2 = new Address("street2", "city2", "zipCode2", "detail2");
        School school2 = new School("연세대학교", StudentType.COLLEGE, 3);
        member2 = Member.builder()
                .loginId("user2")
                .password(passwordEncoder.encode("password2"))
                .role(Role.USER)
                .birth(LocalDate.of(1992, 5, 10))
                .email("user2@email.com")
                .address(address2)
                .gender(Gender.WOMAN)
                .phoneNumber("01022222222")
                .name("박영희")
                .organization(school2)
                .build();

        Address address3 = new Address("street3", "city3", "zipCode3", "detail3");
        School school3 = new School("서울대학교", StudentType.COLLEGE, 2);
        member3 = Member.builder()
                .loginId("admin1")
                .password(passwordEncoder.encode("password3"))
                .role(Role.ADMIN)
                .birth(LocalDate.of(1985, 12, 25))
                .email("admin1@email.com")
                .address(address3)
                .gender(Gender.MAN)
                .phoneNumber("01033333333")
                .name("이관리")
                .organization(school3)
                .build();

        memberRepository.saveAll(List.of(member1, member2, member3));
        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("이름으로 회원을 검색한다")
    @Test
    void searchMemberByName() {
        // given
        AdminMemberSearchCond cond = AdminMemberSearchCond.builder()
                .searchType("name")
                .keyword("김철수")
                .page(0)
                .pageSize(10)
                .build();

        // when
        MemberInfoListResponse response = memberRepository.searchMemberByAdminCond(cond);

        // then
        assertThat(response.getMemberInfos()).hasSize(1);
        assertThat(response.getMemberInfos().get(0).getLoginId()).isEqualTo(member1.getLoginId());
        assertThat(response.getMemberInfos().get(0).getName()).isEqualTo(member1.getName());
    }

    @DisplayName("로그인 아이디로 회원을 검색한다")
    @Test
    void searchMemberByLoginId() {
        // given
        AdminMemberSearchCond cond = AdminMemberSearchCond.builder()
                .searchType("loginId")
                .keyword("admin")
                .page(0)
                .pageSize(10)
                .build();

        // when
        MemberInfoListResponse response = memberRepository.searchMemberByAdminCond(cond);

        // then
        assertThat(response.getMemberInfos()).hasSize(1);
        assertThat(response.getMemberInfos().get(0).getLoginId()).isEqualTo(member3.getLoginId());
        assertThat(response.getMemberInfos().get(0).getName()).isEqualTo(member3.getName());
    }

    @DisplayName("조직 이름으로 회원을 검색한다")
    @Test
    void searchMemberByOrganizationName() {
        // given
        AdminMemberSearchCond cond = AdminMemberSearchCond.builder()
                .searchType("organizationName")
                .keyword("서울대학교")
                .page(0)
                .pageSize(10)
                .build();

        // when
        MemberInfoListResponse response = memberRepository.searchMemberByAdminCond(cond);

        // then
        assertThat(response.getMemberInfos()).hasSize(2);
        List<String> loginIds = response.getMemberInfos().stream()
                .map(MemberInfoListDto::getLoginId)
                .toList();
        assertThat(loginIds).contains(member1.getLoginId(), member3.getLoginId());
    }

    @DisplayName("성별로 회원을 필터링한다")
    @Test
    void filterMemberByGender() {
        // given
        AdminMemberSearchCond cond = AdminMemberSearchCond.builder()
                .gender(Gender.WOMAN)
                .page(0)
                .pageSize(10)
                .build();

        // when
        MemberInfoListResponse response = memberRepository.searchMemberByAdminCond(cond);

        // then
        assertThat(response.getMemberInfos()).hasSize(1);
        assertThat(response.getMemberInfos().get(0).getLoginId()).isEqualTo(member2.getLoginId());
        assertThat(response.getMemberInfos().get(0).getGender()).isEqualTo(member2.getGender());
    }

    @DisplayName("등록 기간으로 회원을 필터링한다")
    @Test
    void filterMemberByRegistrationDate() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);

        AdminMemberSearchCond cond = AdminMemberSearchCond.builder()
                .startDate(yesterday)
                .endDate(tomorrow)
                .page(0)
                .pageSize(10)
                .build();

        // when
        MemberInfoListResponse response = memberRepository.searchMemberByAdminCond(cond);

        // then
        assertThat(response.getMemberInfos()).hasSize(3); // 모든 회원이 조회되어야 함
    }

    @DisplayName("복합 조건으로 회원을 검색한다")
    @Test
    void searchMemberByMultipleConditions() {
        // given
        AdminMemberSearchCond cond = AdminMemberSearchCond.builder()
                .searchType("organizationName")
                .keyword("서울대학교")
                .gender(Gender.MAN)
                .page(0)
                .pageSize(10)
                .build();

        // when
        MemberInfoListResponse response = memberRepository.searchMemberByAdminCond(cond);

        // then
        assertThat(response.getMemberInfos()).hasSize(2);
        List<String> loginIds = response.getMemberInfos().stream()
                .map(MemberInfoListDto::getLoginId)
                .toList();
        assertThat(loginIds).contains(member1.getLoginId(), member3.getLoginId());
    }

    @DisplayName("페이징 처리가 제대로 동작한다")
    @Test
    void testPagination() {
        // given
        AdminMemberSearchCond cond = AdminMemberSearchCond.builder()
                .page(0)
                .pageSize(2)
                .build();

        // when
        MemberInfoListResponse response = memberRepository.searchMemberByAdminCond(cond);

        // then
        assertThat(response.getMemberInfos()).hasSize(2);
        assertThat(response.getTotalPage()).isEqualTo(2); // 총 3개 데이터, 페이지당 2개 -> 2페이지
        assertThat(response.getSize()).isEqualTo(3);
    }
}