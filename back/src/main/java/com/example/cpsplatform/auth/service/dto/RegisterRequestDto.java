package com.example.cpsplatform.auth.service.dto;

import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.organization.Organization;
import com.example.cpsplatform.member.service.dto.MemberSaveDto;
import jakarta.validation.constraints.NotBlank;
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
    private String street;
    private String city;
    private String zipCode;
    private String detail;
    private String phoneNumber;
    private String email;
    private Organization organization;

    public boolean isPasswordsMatch(){
        return password.equals(confirmPassword);
    }

    public MemberSaveDto toMemberSaveDto(){
        return new MemberSaveDto(loginId,password,name,birth,gender,
                street,city,zipCode,detail,phoneNumber,email,organization);
    }
}
