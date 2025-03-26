package com.example.cpsplatform.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "member_login_id",nullable = false,unique = true,updatable = false,length = 12)
    private String loginId;

    @Column(name = "member_login_password",nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "member_signup_complete")
    private Boolean isSignupComplete;

    @Builder
    private Member(final String loginId, final String password, final Role role, final Boolean isSignupComplete) {
        this.loginId = loginId;
        this.password = password;
        this.role = role;
        this.isSignupComplete = isSignupComplete;
    }

    public static Member of(final String loginId, final String password){
        return Member.builder()
                .loginId(loginId)
                .password(password)
                .isSignupComplete(false)
                .role(Role.USER)
                .build();
    }

    public void completeSignUp(){
        this.isSignupComplete = true;
    }

    public boolean isCompleteSignup() {
        return this.isSignupComplete;
    }

    public String getRoleName(){
        return this.role.getName();
    }
}
