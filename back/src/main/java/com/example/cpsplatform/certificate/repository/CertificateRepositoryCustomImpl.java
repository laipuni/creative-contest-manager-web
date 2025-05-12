package com.example.cpsplatform.certificate.repository;

import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateDto;
import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateCond;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateDto;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateResponse;
import com.example.cpsplatform.certificate.repository.dto.UserSearchCertificateCond;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.cpsplatform.certificate.domain.QCertificate.*;
import static com.example.cpsplatform.contest.QContest.contest;
import static com.example.cpsplatform.member.domain.QMember.member;
import static com.example.cpsplatform.team.domain.QTeam.team;

@Slf4j
public class CertificateRepositoryCustomImpl implements CertificateRepositoryCustom{
    private static final Map<String, Expression<?>> ORDER_FIELD_MAP = Map.of(
            "createdAt", certificate.createdAt,
            "title", certificate.title
    );

    private final JPAQueryFactory queryFactory;

    public CertificateRepositoryCustomImpl(final EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 유저 용 확인증 검색 기능
     * 유저가 소유한 확인증을 타입에 따라 조회 할 수 있다.
     * 현재 가능한 검색 타입 : 확인증 타입
     * 현재 가능한 정렬 타입 : 생성 일자
     */
    @Override
    public UserSearchCertificateResponse SearchUserCertificate(UserSearchCertificateCond cond) {
        log.debug("유저({})가 확인증({})을 리스트 조회(page : {}, page = {}, order = {})"
                ,cond.getUsername(),cond.getCertificateType(),cond.getPage(),cond.getPageSize(),cond.getOrder());
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getPageSize());
        List<UserSearchCertificateDto> result = queryFactory.select(
                        Projections.constructor(UserSearchCertificateDto.class,
                                certificate.id,
                                certificate.title,
                                certificate.certificateType,
                                certificate.createdAt,
                                team.name
                        )
                )
                .from(certificate)
                .join(certificate.member, member)
                .leftJoin(certificate.contest, contest)
                .join(certificate.team, team)
                .where(filterUserBy(cond.getCertificateType(),cond.getUsername()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(certificate.createdAt.asc())
                .fetch();

        //페이징 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory.select(certificate.count())
                .from(certificate)
                .join(certificate.member, member)
                .leftJoin(certificate.contest, contest)
                .join(certificate.team, team)
                .where(filterUserBy(cond.getCertificateType(),cond.getUsername()));

        Page<UserSearchCertificateDto> certificateDtoPage = PageableExecutionUtils.getPage(
                result, pageable, countQuery::fetchOne
        );

        return UserSearchCertificateResponse.of(certificateDtoPage);
    }

    private BooleanBuilder filterUserBy(final CertificateType certificateType,final String username){
        return filterWithCertificateType(certificateType)
                .and(filterWithUsername(username));
    }

    private BooleanBuilder filterWithCertificateType(final CertificateType certificateType){
        if(certificateType == null){
            return new BooleanBuilder();
        }
        return new BooleanBuilder(certificate.certificateType.eq(certificateType));
    }

    private BooleanBuilder filterWithUsername(final String username){

        return new BooleanBuilder(member.loginId.eq(username));
    }

    /**
     * 관리자 용 확인증 검색 기능
     * 검색 타입에 따라 검색어로 확인증을 검색할 수 있다.
     * 현재 가능한 검색 타입 : 유저 아이디, 유저 이름
     * 현재 가능한 정렬 타입 : 생성 일자, 제목
     */
    @Override
    public AdminSearchCertificateResponse SearchAdminCertificate(final AdminSearchCertificateCond cond) {
        log.info("확인증({})을 리스트 조회(page : {}, page = {}, order = {})", cond.getPage(),cond.getPageSize(),cond.getOrderType());

        Pageable pageable = PageRequest.of(cond.getPage(), cond.getPageSize());
        List<AdminSearchCertificateDto> result = queryFactory.select(
                        Projections.constructor(AdminSearchCertificateDto.class,
                                certificate.id,
                                certificate.title,
                                certificate.certificateType,
                                certificate.createdAt,
                                team.name
                        )
                )
                .from(certificate)
                .join(certificate.member, member)
                .leftJoin(certificate.contest, contest)
                .join(certificate.team, team)
                .where(filterAdminBy(cond.getSearchType(), cond.getKeyword(), cond.getCertificateType()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderAdminBy(cond.getOrderType(), cond.getDirection()))
                .fetch();

        //페이징 카운팅 쿼리
        JPAQuery<Long> countQuery = queryFactory.select(certificate.count())
                .from(certificate)
                .join(certificate.member, member)
                .leftJoin(certificate.contest, contest)
                .join(certificate.team, team)
                .where(filterAdminBy(cond.getSearchType(), cond.getKeyword(),cond.getCertificateType()));

        Page<AdminSearchCertificateDto> certificateDtoPage = PageableExecutionUtils.getPage(
                result, pageable, countQuery::fetchOne
        );

        return AdminSearchCertificateResponse.of(certificateDtoPage);
    }

    private BooleanBuilder[] filterAdminBy(final String searchType, final String keyword, final CertificateType certificateType){
        List<BooleanBuilder> builders = new ArrayList<>();

        if(certificateType != null){
            //특정 확인증에 대한 필터링
            builders.add(new BooleanBuilder(certificate.certificateType.eq(certificateType)));
        }

        if (searchType.equals("loginId")) {
            //유저의 아이디 필터링
            builders.add(new BooleanBuilder(member.loginId.contains(keyword)));
        } else if(searchType.equals("name")){
            //유저의 이름 필터링
            builders.add(new BooleanBuilder(member.name.contains(keyword)));
        }

        return builders.toArray(BooleanBuilder[]::new);
    }

    private OrderSpecifier<?>[] orderAdminBy(String orderType, String direction) {
        if (!StringUtils.hasText(orderType) || !StringUtils.hasText(direction)) {
            return new OrderSpecifier[0];
        }

        Order order = "asc".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;
        Expression<?> expression = ORDER_FIELD_MAP.get(orderType);

        if (expression == null) {
            log.info("지원하지 않는 정렬 타입: {}", orderType);
            return new OrderSpecifier[0];
        }

        return new OrderSpecifier[]{ new OrderSpecifier(order, expression) };
    }
}
