package com.example.cpsplatform.security.domain;

import com.example.cpsplatform.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class SecurityMember extends User {

    private Long userSeq;
    private String name;

    public SecurityMember(final Member member) {
        super(member.getLoginId(), member.getPassword(), getAuthorize(member));
        this.userSeq = member.getId();
        this.name = member.getName();
    }

    private static List<SimpleGrantedAuthority> getAuthorize(Member member){
        return List.of(new SimpleGrantedAuthority(member.getRoleName()));
    }
}
