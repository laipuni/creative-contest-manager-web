package com.example.cpsplatform.member.admin.controller.response;

import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Role;
import com.example.cpsplatform.member.domain.organization.Organization;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberInfoListDto {

    private String loginId;
    private String name;
    private Role role;
    private LocalDate birth;
    private Gender gender;
    private Organization organization;
    private LocalDateTime createdAt;

}
