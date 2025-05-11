package com.example.cpsplatform.member.service.dto;

import com.example.cpsplatform.member.domain.Address;
import com.example.cpsplatform.member.domain.Gender;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.organization.Organization;
import com.example.cpsplatform.security.encoder.CryptoService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MemberSaveDto {

    private String loginId;
    private String password;
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

    public Member toEntity(CryptoService cryptoService, PasswordEncoder passwordEncoder){
        return Member.of(loginId,password,
                street, city, zipCode, detail,
                name,email,gender, birth,
                phoneNumber,organization,
                cryptoService,passwordEncoder
        );
    }

    public Address toEncryptedAddress(CryptoService cryptoService) {
        return new Address(
                cryptoService.encryptAES(street),
                city,
                zipCode,
                cryptoService.encryptAES(detail)
        );
    }

}
