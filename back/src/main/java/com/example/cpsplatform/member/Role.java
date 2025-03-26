package com.example.cpsplatform.member;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("유저","ROLE_USER"),
    ADMIN("관리자","ROLE_ADMIN");

    final String description;
    final String name;

}
