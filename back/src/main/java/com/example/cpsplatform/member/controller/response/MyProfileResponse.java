package com.example.cpsplatform.member.controller.response;

import com.example.cpsplatform.member.domain.Address;
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
    private String city;
    private String zipCode;
    private String detail;
    private String phoneNumber;
    private String email;
    private String organizationType;
    private String organizationName;
    private String position;
    private String session;

    public static MyProfileResponse of(Member member, CryptoService cryptoService){

        Organization organization = member.getOrganization();

        String phoneNumber = cryptoService.decryptAES(member.getPhoneNumber());
        String email = cryptoService.decryptAES(member.getEmail());
        Address address = member.getAddress();
        String street = cryptoService.decryptAES(address.getStreet());
        String detail = cryptoService.decryptAES(address.getDetail());

        return MyProfileResponse.builder()
                .name(member.getName())
                .birth(member.getBirth())
                .gender(member.getGender().getDescription())
                .street(street)
                .city(address.getCity())
                .zipCode(address.getZipCode())
                .detail(detail)
                .phoneNumber(phoneNumber)
                .email(email)
                .organizationType(organization.getOrganizationType())
                .organizationName(member.getOrganization().getName())
                .position(organization.getPosition())
                .build();
    }

    public void setSession(String session){
        this.session = session;
    }
}
