package com.example.cpsplatform.notice.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSearchNoticeCond {

    private int page;
    private int pageSize;
    private String keyword;
    private String searchType;
    private String order;
    private String orderType;

    public static UserSearchNoticeCond of(final int page,
                                           final int pageSize,
                                           final String keyword,
                                           final String searchType,
                                           final String order,
                                           final String orderType){
        return UserSearchNoticeCond.builder()
                .page(page)
                .pageSize(pageSize)
                .keyword(keyword)
                .searchType(searchType)
                .order(order)
                .orderType(orderType)
                .build();
    }

}
