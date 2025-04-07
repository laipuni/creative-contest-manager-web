package com.example.cpsplatform.member.controller.request;

import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.organization.Organization;
import com.example.cpsplatform.member.domain.organization.company.Company;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.auth.service.dto.RegisterRequestDto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ValidOrganization
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegisterReqeust {

    @Size(min = 4, max = 12, message = "로그인 ID는 4-12자 이내여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "로그인 ID는 영문자와 숫자만 가능합니다")
    private String loginId;

    @Size(min = 4, max = 8, message = "비밀번호는 4-12자 이내여야 합니다")
    private String password;

    @NotBlank(message = "비밀번호확인은 필수입니다")
    private String confirmPassword;

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotNull(message = "생년월일은 필수입니다")
    @Past(message = "생년월일은 과거 날짜여야 합니다")
    private LocalDate birth;

    @NotNull(message = "성별은 필수입니다")
    private Gender gender;

    @NotBlank(message = "도로명 주소는 필수입니다")
    private String street;

    @NotBlank(message = "도시명은 필수입니다")
    private String city;

    @NotBlank(message = "우편번호는 필수입니다")
    private String zipCode;

    @NotBlank(message = "상세주소는 필수입니다")
    private String detail;

    @Pattern(regexp = "^010\\d{8}$", message = "휴대폰 번호는 010으로 시작하는 11자리 숫자여야 합니다")
    private String phoneNumber;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotBlank(message = "직업은 필수입니다")
    private String organizationType;

    @NotBlank(message = "학교(소속) 이름은 필수입니다")
    private String organizationName;

    @NotBlank(message = "학년(부서)는 필수입니다")
    private String position;

    public RegisterRequestDto toRegisterRequest(){
        StudentType studentType = StudentType.findStudentTypeBy(organizationType);
        Organization organization = getOrganization(studentType);
        return new RegisterRequestDto(loginId,password,confirmPassword,
                name,birth,gender,street,city,zipCode,detail,phoneNumber,email,organization);
    }

    private Organization getOrganization(final StudentType studentType){
        if(studentType != null){
            return new School(organizationName, studentType, Integer.parseInt(position));
        } else{
            return new Company(organizationName, position);
        }
    }
}