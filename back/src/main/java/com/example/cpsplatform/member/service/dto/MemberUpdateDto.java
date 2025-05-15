package com.example.cpsplatform.member.service.dto;

import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.organization.Organization;
import com.example.cpsplatform.member.domain.organization.company.Company;
import com.example.cpsplatform.member.domain.organization.company.FieldType;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 유저의 정보를 저장한 Member를 수정하는 dto class
 * dto로 업데이트 한 이유는 파라미터가 많아져서 dto로 빼냈다.
 * 업데이트 용 dto는 해당 클래스로 제한한다.
 */
@Getter
public class MemberUpdateDto {

    private Long memberId;
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

    public MemberUpdateDto(final Long memberId, final String name, final LocalDate birth, final Gender gender, final String street,
                           final String city, final String zipCode, final String detail, final String phoneNumber,
                           final String email, final String organizationType, final String organizationName, final String position) {
        this.memberId = memberId;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.detail = detail;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.organizationType = organizationType;
        this.organizationName = organizationName;
        this.position = position;
    }

    public static MemberUpdateDto of(final Long memberId, final String name, final LocalDate birth, final Gender gender, final String street,
                                     final String city, final String zipCode, final String detail, final String phoneNumber,
                                     final String email, final String organizationType, final String organizationName, final String position){
        return new MemberUpdateDto(
                memberId,
                name, birth, gender,
                street, city, zipCode, detail,
                phoneNumber, email, organizationType,
                organizationName,position
        );
    }
}
