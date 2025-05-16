package com.example.cpsplatform.member.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.member.domain.organization.Organization;
import com.example.cpsplatform.member.domain.organization.company.Company;
import com.example.cpsplatform.member.domain.organization.company.FieldType;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.member.service.dto.MemberUpdateDto;
import com.example.cpsplatform.security.encoder.CryptoService;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Year;

@Entity
@Getter
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_login_id", columnNames = {"login_id"}),
        @UniqueConstraint(name = "uk_member_email", columnNames = {"email"}),
        @UniqueConstraint(name = "uk_member_phone_number", columnNames = {"phone_number"}),
        @UniqueConstraint(name = "uk_member_organization_id", columnNames = {"organization_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id",nullable = false,updatable = false,length = 12)
    private String loginId;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, name = "email")
    private String email;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birth;

    @Embedded
    private Address address;

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Builder
    private Member(final String loginId, final String password, final String name, final String email,
                  final Role role, final Gender gender,
                  final LocalDate birth, final Address address, final String phoneNumber, final Organization organization) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.birth = birth;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.organization = organization;
    }

    public static Member of(final String loginId, final String password,
                            final String street, final String city, final String zipCode, final String detail,
                            final String name, final String email, final Gender gender, final LocalDate birth,
                            final String phoneNumber, final Organization organization,
                            final CryptoService cryptoService, PasswordEncoder passwordEncoder){
        //주소 암호화
        Address address = toEncryptedAddress(street, city, zipCode, detail, cryptoService);
        //핸드폰 번호 암호화
        String encodedPhoneNumber = cryptoService.encryptAES(phoneNumber);
        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        return Member.builder()
                .loginId(loginId)
                .email(email)
                .password(encodedPassword)
                .gender(gender)
                .birth(birth)
                .name(name)
                .phoneNumber(encodedPhoneNumber)
                .address(address)
                .role(Role.USER)
                .organization(organization)
                .build();
    }

    public String getRoleName(){
        return this.role.getName();
    }

    public boolean isSignedUpThisYear(int currentYear) {
        return this.getCreatedAt().getYear() == currentYear;  //가입 연도와 현재 연도와 같은지 비교
    }


    public void changePassword(final String newPassword){
        if(newPassword != null){
            this.password = newPassword;
        }
    }

    public void changeEmail(final String newEmail){
        if(newEmail != null){
            this.email = newEmail;
        }
    }

    public void update(final MemberUpdateDto dto, final CryptoService cryptoService) {
        Organization organization = createOrganization(
                dto.getOrganizationName(),
                dto.getPosition(),
                dto.getOrganizationType()
        );

        Address address = toEncryptedAddress(
                dto.getStreet(),
                dto.getCity(),
                dto.getZipCode(),
                dto.getDetail(),
                cryptoService
        );

        changeEmail(dto.getEmail());
        this.name = StringUtils.hasText(dto.getName()) ? dto.getName() : this.name;
        this.gender = dto.getGender() != null ? dto.getGender() : this.gender;
        this.birth = dto.getBirth() != null ? dto.getBirth() : this.birth;
        this.address = address;
        this.phoneNumber = StringUtils.hasText(dto.getPhoneNumber()) ? dto.getPhoneNumber() : this.phoneNumber;
        this.organization = organization;
    }


    private static Organization createOrganization(String organizationName, String position, String organizationType) {
        StudentType studentType = StudentType.findStudentTypeBy(organizationType);
        FieldType fieldType = FieldType.findFiledType(organizationType);
        //타입에 따라 조직 변경
        if (studentType != null) {
            //학생일 경우
            int grade = parsePositionToGrade(position);
            return new School(organizationName, studentType, grade);
        }
        if (fieldType != null) {
            //직장인일경우
            return new Company(organizationName, position, fieldType);
        }
        throw new IllegalArgumentException("직업 유형이 유효하지 않습니다: " + organizationType);
    }

    private static int parsePositionToGrade(String position) {
        try {
            return Integer.parseInt(position);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("학생의 학년(grade)을 숫자로 입력해주세요: " + position);
        }
    }

    public static Address toEncryptedAddress(String street, String city, String zipCode, String detail,CryptoService cryptoService) {
        return new Address(
                cryptoService.encryptAES(street),
                city,
                zipCode,
                cryptoService.encryptAES(detail)
        );
    }

    public boolean isChangedEmail(final String email) {
        return StringUtils.hasText(email) && !this.email.equals(email);
    }

}
