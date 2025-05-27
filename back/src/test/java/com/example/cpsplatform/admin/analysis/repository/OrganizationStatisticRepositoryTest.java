package com.example.cpsplatform.admin.analysis.repository;

import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionDto;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.Organization;
import com.example.cpsplatform.member.domain.organization.company.Company;
import com.example.cpsplatform.member.domain.organization.company.FieldType;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrganizationStatisticRepositoryTest {

    @Autowired
    OrganizationStatisticRepository organizationStatisticRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager entityManager;

    @DisplayName("유저들의 조직 분포도 분석 값을 조회한다.")
    @Test
    void getOrganizationStatistics(){
        //given
        School school1 = new School("xx대학교", StudentType.COLLEGE, 4);
        School school2 = new School("xx고등학교", StudentType.HIGH, 3);
        School school3 = new School("xxx중학교", StudentType.MIDDLE, 2);
        School school4 = new School("xx초등학교", StudentType.ELEMENTARY, 5);

        Company company1 = new Company("삼성전자", "사원", FieldType.COMPUTER);
        Company company2 = new Company("ABS", "기자", FieldType.MEDIA);
        Company company3 = new Company("xx부대", "대위", FieldType.MILITARY);
        Company company4 = new Company("xx행정", "6급", FieldType.PUBLIC_SERVANT);
        Company company5 = new Company("xxx서비스 업체", "과장", FieldType.SERVICE);
        Company company6 = new Company("xx미술관", "관장", FieldType.ART);
        Company company7 = new Company("기타", "관장", FieldType.ETC);



        //School에 속한 멤버 4명
        createAndSaveMember("school1", "홍길동", 1, school1);
        createAndSaveMember("school2", "임꺽정", 2, school2);
        createAndSaveMember("school3", "장길산", 3, school3);
        createAndSaveMember("school4", "장길산", 4, school4);

        //Company에 속한 멤버 7명
        createAndSaveMember("company1", "김철수", 5, company1);
        createAndSaveMember("company2", "이영수", 6, company2);
        createAndSaveMember("company3", "이찬희", 7, company3);
        createAndSaveMember("company4", "김영희", 8, company4);
        createAndSaveMember("company5", "이민수", 9, company5);
        createAndSaveMember("company6", "박영철", 10, company6);
        createAndSaveMember("company7", "구경욱", 11, company7);

        entityManager.flush();
        entityManager.clear();

        //when
        List<OrganizationDistributionDto> result = organizationStatisticRepository.getOrganizationStatistics();

        //then
        Assertions.assertThat(result).hasSize(11)
                .extracting("organizationType","count")
                .containsExactlyInAnyOrder(tuple("COLLEGE",1L),
                        tuple("HIGH",1L),
                        tuple("MIDDLE",1L),
                        tuple("ELEMENTARY",1L),
                        tuple("COMPUTER",1L),
                        tuple("MEDIA",1L),
                        tuple("MILITARY",1L),
                        tuple("PUBLIC_SERVANT",1L),
                        tuple("SERVICE",1L),
                        tuple("ART",1L),
                        tuple("ETC",1L)
                );
    }

    private Member createAndSaveMember(String loginId, String name, int num, Organization organization) {
        Address address = new Address("street", "city", "zipCode", "detail");
        String phoneNumber = "010" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 8);

        Member member = Member.builder()
                .loginId(loginId + num)
                .password("1234" + num)
                .role(Role.USER)
                .birth(LocalDate.of(2000, 1, 1))
                .email(num + "@email.com")
                .address(address)
                .gender(num % 2 == 0 ? Gender.MAN : Gender.WOMAN)
                .phoneNumber(phoneNumber)
                .name(name)
                .organization(organization)
                .build();

        return memberRepository.save(member);
    }

}