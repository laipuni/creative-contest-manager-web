package com.example.cpsplatform.member.service.dto;

import com.example.cpsplatform.member.domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class UpdateMyProfileDto {

    private String loginId;
    private String name;
    private LocalDate birth;
    private Gender gender;
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

    public MemberUpdateDto toMemberUpdateDto(Long memberId) {
        return MemberUpdateDto.of(
                memberId,
                name, birth, gender,
                street, city, zipCode, detail,
                phoneNumber, email, organizationType,
                organizationName,position
        );
    }

}
