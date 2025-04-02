package com.example.cpsplatform.member.service.dto;

import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.organization.Organization;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MemberSaveDto {

    private String loginId;
    private String password;
    private String name;
    private LocalDate birth;
    private Gender gender;
    private Address address;
    private String phoneNumber;
    private String email;
    private Organization organization;

    public Member toEntity(){
        return Member.of(this.loginId,this.password,this.name,this.email,this.gender,
                this.birth,address,this.phoneNumber,organization);
    }

}
