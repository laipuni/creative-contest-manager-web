package com.example.cpsplatform.member.controller.response;

import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.organization.Organization;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyProfileResponse {

    private String name;
    private LocalDate birth;
    private String gender;
    private String street;
    private String zipCode;
    private String detail;
    private String phoneNumber;
    private String email;
    private String organizationType;
    private String organizationName;
    private String position;

    public static MyProfileResponse of(Member member){

        Organization organization = member.getOrganization();

        return MyProfileResponse.builder()
                .name(member.getName())
                .birth(member.getBirth())
                .gender(member.getGender().getDescription())
                .street(member.getAddress().getStreet())
                .zipCode(member.getAddress().getZipCode())
                .detail(member.getAddress().getDetail())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .organizationType(organization.getOrganizationType())
                .organizationName(member.getOrganization().getName())
                .position(organization.getPosition())
                .build();
    }
}
