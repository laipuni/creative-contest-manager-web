package com.example.cpsplatform.notice.repository;

import com.example.cpsplatform.notice.admin.controller.response.NoticeSearchDto;
import com.example.cpsplatform.notice.admin.controller.response.NoticeSearchResponse;
import com.example.cpsplatform.notice.repository.dto.AdminSearchNoticeCond;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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

import java.util.List;

import static com.example.cpsplatform.member.domain.QMember.member;
import static com.example.cpsplatform.notice.domain.QNotice.notice;

public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public NoticeRepositoryCustomImpl(final EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 관리자 용 공지사항 검색
     * 검색 타입 : 제목
     * 정렬 타입 : 조회수, 생성 일자
     * @param cond 관리자 공지사항 검색 조건
     * @return 공지사항 검색 결과
     */
    @Override
    public NoticeSearchResponse searchNoticeByAdminCond(final AdminSearchNoticeCond cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getPageSize());

        List<NoticeSearchDto> content = queryFactory.select(
                        Projections.constructor(NoticeSearchDto.class,
                                notice.id,
                                notice.title,
                                notice.viewCount,
                                notice.writer.name,
                                notice.createdAt
                        )
                )
                .from(notice)
                .where(filterByAdminCond(cond))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderByAdminCond(cond))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(notice.count())
                .from(notice)
                .where()
                .orderBy();

        Page<NoticeSearchDto> searchDtoPage = PageableExecutionUtils.getPage(
                content, pageable, countQuery::fetchOne
        );

        return NoticeSearchResponse.of(searchDtoPage);
    }

    private BooleanBuilder filterByAdminCond(final AdminSearchNoticeCond cond){
        return fiterByAdminCondKeyword(cond);
    }

    private BooleanBuilder fiterByAdminCondKeyword(final AdminSearchNoticeCond cond){
        if(!StringUtils.hasText(cond.getSearchType()) || !StringUtils.hasText(cond.getKeyword())){
            return new BooleanBuilder();
        }
        return switch (cond.getSearchType()) {
            case "title" -> //유저의 이름
                    new BooleanBuilder(notice.title.contains(cond.getKeyword()));
            default -> new BooleanBuilder();
        };
    }

    private OrderSpecifier[] orderByAdminCond(final AdminSearchNoticeCond cond){
        if(!StringUtils.hasText(cond.getOrderType()) || !StringUtils.hasText(cond.getOrder())){
            return new OrderSpecifier[0];
        }
        Order direction = "asc".equalsIgnoreCase(cond.getOrder()) ? Order.ASC : Order.DESC;
        return switch (cond.getOrderType()) {
            case "createdAt" -> //생성 일자
                    new OrderSpecifier[]{new OrderSpecifier<>(direction, notice.createdAt)};
            case "viewCount" -> // 조회수
                    new OrderSpecifier[]{new OrderSpecifier<>(direction, notice.viewCount)};
            default -> new OrderSpecifier[0];
        };
    }

}
