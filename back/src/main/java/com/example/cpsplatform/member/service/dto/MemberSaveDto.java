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
        Address address = toEncryptedAddress(cryptoService);
        String encodedPhoneNumber = cryptoService.encryptAES(phoneNumber);
        String encodedPassword = passwordEncoder.encode(password);
        return Member.of(this.loginId,encodedPassword,this.name,this.email,this.gender,
                this.birth,address,encodedPhoneNumber,organization);
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
