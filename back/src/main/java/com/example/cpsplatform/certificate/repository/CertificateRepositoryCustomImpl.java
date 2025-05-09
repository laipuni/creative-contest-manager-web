package com.example.cpsplatform.certificate.repository;

import com.example.cpsplatform.PagingUtils;
import com.example.cpsplatform.certificate.controller.response.SearchCertificateDto;
import com.example.cpsplatform.certificate.controller.response.SearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.domain.QCertificate;
import com.example.cpsplatform.contest.QContest;
import com.example.cpsplatform.member.domain.QMember;
import com.example.cpsplatform.team.domain.QTeam;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.cpsplatform.certificate.domain.QCertificate.*;
import static com.example.cpsplatform.contest.QContest.contest;
import static com.example.cpsplatform.member.domain.QMember.member;
import static com.example.cpsplatform.team.domain.QTeam.team;

@Slf4j
public class CertificateRepositoryCustomImpl implements CertificateRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public CertificateRepositoryCustomImpl(final EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public SearchCertificateResponse SearchCertificate(final int page,final int pageSize, final String order,
                                                        final CertificateType certificateType, final String username) {
        log.debug("유저({})가 확인증({})을 리스트 조회(page : {}, page = {}, order = {})",username,certificateType,page,pageSize,order);
        Pageable pageable = PageRequest.of(page, pageSize);
        List<SearchCertificateDto> result = queryFactory.select(
                        Projections.constructor(SearchCertificateDto.class,
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
                .where(filterBy(certificateType,username))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(certificate.createdAt.asc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(certificate.count())
                .from(certificate)
                .join(certificate.member, member)
                .leftJoin(certificate.contest, contest)
                .join(certificate.team, team)
                .where(filterBy(certificateType,username));

        Page<SearchCertificateDto> certificateDtoPage = PageableExecutionUtils.getPage(
                result, pageable, countQuery::fetchOne
        );

        return SearchCertificateResponse.of(certificateDtoPage);
    }

    private BooleanBuilder filterBy(final CertificateType certificateType,String username){
        return filterWithCertificateType(certificateType)
                .and(filterWithUsername(username));
    }

    private BooleanBuilder filterWithCertificateType(final CertificateType certificateType){
        if(certificateType == null){
            return new BooleanBuilder();
        }
        return new BooleanBuilder(certificate.certificateType.eq(certificateType));
    }

    private BooleanBuilder filterWithUsername(String username){
        return new BooleanBuilder(member.loginId.eq(username));
    }
}
