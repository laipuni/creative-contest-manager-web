package com.example.cpsplatform.member.repository.dto;

import com.example.cpsplatform.member.domain.Gender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminMemberSearchCond {

    private int page;
    private int pageSize;
    private String order;
    private String keyword;
    private String searchType;
    private Gender gender;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    public static AdminMemberSearchCond of(final int page, final int pageSize, final String order,
                                           final String keyword, final String searchType,final Gender gender,
                                           final LocalDateTime startDate, final LocalDateTime endDate) {
        return AdminMemberSearchCond.builder()
                .page(page)
                .pageSize(pageSize)
                .order(order)
                .keyword(keyword)
                .searchType(searchType)
                .gender(gender)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
