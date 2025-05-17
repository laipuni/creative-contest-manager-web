package com.example.cpsplatform.member.repository;

import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateDto;
import com.example.cpsplatform.member.admin.controller.response.MemberInfoListDto;
import com.example.cpsplatform.member.admin.controller.response.MemberInfoListResponse;
import com.example.cpsplatform.member.domain.QMember;
import com.example.cpsplatform.member.repository.dto.AdminMemberSearchCond;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.cpsplatform.member.domain.QMember.member;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryCustomImpl(final EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public MemberInfoListResponse searchMemberByAdminCond(final AdminMemberSearchCond cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getPageSize());
        List<MemberInfoListDto> content = queryFactory.select(
                        Projections.constructor(MemberInfoListDto.class,
                                member.loginId,
                                member.name,
                                member.role,
                                member.birth,
                                member.gender,
                                member.organization,
                                member.createdAt
                        )
                )
                .from(member)
                .join(member.organization)
                .where(filterBy(cond))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderBy(cond))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(member.count())
                .from(member)
                .join(member.organization)
                .where();

        Page<MemberInfoListDto> certificateDtoPage = PageableExecutionUtils.getPage(
                content, pageable, countQuery::fetchOne
        );

        return MemberInfoListResponse.of(certificateDtoPage);
    }

    private BooleanBuilder filterBy(final AdminMemberSearchCond cond) {
        return filterByKeyword(cond)
                .and(filterByGender(cond))
                .and(filterByRegistrationStartAt(cond))
                .and(filterByRegistrationEndAt(cond));
    }

    private BooleanBuilder filterByRegistrationStartAt(final AdminMemberSearchCond cond){
        if(cond.getStartDate() != null){
            new BooleanBuilder(member.createdAt.after(cond.getStartDate()));
        }
        return new BooleanBuilder();
    }

    private BooleanBuilder filterByRegistrationEndAt(final AdminMemberSearchCond cond){
        if(cond.getEndDate() != null){
            new BooleanBuilder(member.createdAt.before(cond.getEndDate()));
        }
        return new BooleanBuilder();
    }

    private static BooleanBuilder filterByGender(final AdminMemberSearchCond cond) {
        if(cond.getGender() != null){
            return new BooleanBuilder(member.gender.eq(cond.getGender()));
        }
        return new BooleanBuilder();
    }

    private static BooleanBuilder filterByKeyword(final AdminMemberSearchCond cond) {
        if(!StringUtils.hasText(cond.getSearchType()) || !StringUtils.hasText(cond.getKeyword())){
            //검색 타입 혹은 검색 키워드가 없을 경우, 검색 x
            return new BooleanBuilder();
        }

        return switch (cond.getSearchType()) {
            case "name" -> //유저의 이름
                    new BooleanBuilder(member.name.contains(cond.getKeyword()));
            case "loginId" -> //유저의 아이디
                    new BooleanBuilder(member.loginId.contains(cond.getKeyword()));
            case "organizationName" -> //조직 이름
                    new BooleanBuilder(member.organization.name.contains(cond.getKeyword()));
            default -> new BooleanBuilder();
        };
    }

    private OrderSpecifier[] orderBy(final AdminMemberSearchCond cond){
        Order direction = "desc".equalsIgnoreCase(cond.getOrder()) ? Order.DESC : Order.ASC;
        return new OrderSpecifier[]{new OrderSpecifier<>(direction, member.createdAt)};
    }
}
