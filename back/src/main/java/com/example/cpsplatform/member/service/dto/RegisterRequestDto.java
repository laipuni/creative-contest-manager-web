package com.example.cpsplatform.member.service.dto;

import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.organization.Organization;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RegisterRequestDto {

    private String loginId;
    private String password;
    private String confirmPassword;
    private String name;
    private LocalDate birth;
    private Gender gender;
    private Address address;
    private String phoneNumber;
    private String email;
    private String confirmAuthCode;
    private Organization organization;

    public boolean isPasswordsMatch(){
        return password.equals(confirmPassword);
    }

    public MemberSaveDto toMemberSaveDto(){
        return new MemberSaveDto(loginId,password,name,birth,gender,
                address,phoneNumber,email,organization);
    }

    public void encodingPassword(final String encodingPassword){
        this.password = encodingPassword;
    }
}
