package com.example.cpsplatform.member.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.member.domain.organization.Organization;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "login_id",nullable = false,unique = true,updatable = false,length = 12)
    private String loginId;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true)
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

    @Column(nullable = false, name = "phone_number", unique = true, length = 25)
    private String phoneNumber; // 010-xxxx-xxxx X, 010xxxxXXXX O

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
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

    public static Member of(final String loginId, final String password, final String name, final String email,
                            final Gender gender, final LocalDate birth, final Address address, final String phoneNumber,
                            final Organization organization){

        return Member.builder()
                .loginId(loginId)
                .email(email)
                .password(password)
                .gender(gender)
                .birth(birth)
                .name(name)
                .phoneNumber(phoneNumber)
                .address(address)
                .role(Role.USER)
                .organization(organization)
                .build();
    }

    public String getRoleName(){
        return this.role.getName();
    }

    public void changePassword(final String newPassword){
        if(newPassword != null){
            this.password = newPassword;
        }
    }
}
