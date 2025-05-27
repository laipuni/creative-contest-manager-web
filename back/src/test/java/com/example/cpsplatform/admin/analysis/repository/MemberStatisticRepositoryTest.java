package com.example.cpsplatform.admin.analysis.repository;

import com.example.cpsplatform.admin.analysis.response.CityDistributionDto;
import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.Organization;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberStatisticRepositoryTest {

    @Autowired
    MemberStatisticRepository memberStatisticRepository;

    @Autowired
    EntityManager entityManager;


    @DisplayName("대한민국 주요 도시별 회원 수 분포를 조회한다.")
    @Test
    void getMemberCityDistribution(){
        //given
        createAndSaveMember("user1", "김유신", 1, "서울");
        createAndSaveMember("user2", "이순신", 2, "서울");
        createAndSaveMember("user3", "홍길동", 3, "부산");
        createAndSaveMember("user4", "강감찬", 4, "대구");
        createAndSaveMember("user5", "유관순", 5, "대구");
        createAndSaveMember("user6", "윤봉길", 6, "대구");
        createAndSaveMember("user7", "신사임당", 7, "인천");
        createAndSaveMember("user8", "장보고", 8, "인천");
        createAndSaveMember("user9", "세종대왕", 9, "광주");
        createAndSaveMember("user10", "최무선", 10, "대전");
        createAndSaveMember("user11", "안중근", 11, "대전");
        createAndSaveMember("user12", "김구", 12, "울산");
        createAndSaveMember("user13", "허준", 13, "수원");
        createAndSaveMember("user14", "정약용", 14, "수원");
        createAndSaveMember("user15", "장영실", 15, "수원");
        createAndSaveMember("user16", "이황", 16, "창원");
        createAndSaveMember("user17", "정도전", 17, "청주");

        entityManager.flush();
        entityManager.clear();

        //when
        List<CityDistributionDto> result = memberStatisticRepository.getMemberCityDistribution();

        //then
        assertThat(result)
                .extracting(
                        CityDistributionDto::getCity,
                        CityDistributionDto::getCount
                )
                .containsOnly(
                        tuple("서울", 2L),
                        tuple("부산", 1L),
                        tuple("대구", 3L),
                        tuple("인천", 2L),
                        tuple("광주", 1L),
                        tuple("대전", 2L),
                        tuple("울산", 1L),
                        tuple("수원", 3L),
                        tuple("창원", 1L),
                        tuple("청주", 1L)
                );
    }

    private Member createAndSaveMember(String loginId, String name, int num, String city) {
        Address address = new Address("street", city, "zipCode", "detail");
        School school = new School("xx대학교", StudentType.COLLEGE, 4);
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
                .organization(school)
                .build();

        return memberStatisticRepository.save(member);
    }

}