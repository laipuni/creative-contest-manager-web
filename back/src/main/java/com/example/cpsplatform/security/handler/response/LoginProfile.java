package com.example.cpsplatform.security.handler.response;

import com.example.cpsplatform.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginProfile {

    private String loginId;
    private String role;
}
