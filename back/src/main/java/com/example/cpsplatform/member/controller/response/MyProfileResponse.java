package com.example.cpsplatform.member.controller.response;

import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.organization.Organization;
import java.time.LocalDate;

import com.example.cpsplatform.security.encoder.CryptoService;
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

    public static MyProfileResponse of(Member member, CryptoService cryptoService){

        Organization organization = member.getOrganization();

        String phoneNumber = cryptoService.decryptAES(member.getPhoneNumber());
        String email = cryptoService.decryptAES(member.getEmail());
        String street = cryptoService.decryptAES(member.getAddress().getStreet());
        String detail = cryptoService.decryptAES(member.getAddress().getDetail());

        return MyProfileResponse.builder()
                .name(member.getName())
                .birth(member.getBirth())
                .gender(member.getGender().getDescription())
                .street(street)
                .zipCode(member.getAddress().getZipCode())
                .detail(detail)
                .phoneNumber(phoneNumber)
                .email(email)
                .organizationType(organization.getOrganizationType())
                .organizationName(member.getOrganization().getName())
                .position(organization.getPosition())
                .build();
    }
}
