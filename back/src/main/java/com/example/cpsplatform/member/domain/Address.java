package com.example.cpsplatform.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {

    @Column(nullable = false, name = "address_steet")
    private String street;

    @Column(nullable = false, name = "address_zip_code", length = 20)
    private String zipCode;

    @Column(nullable = false, name = "address_detail")
    private String detail;

    public Address(final String street, final String zipCode, final String detail) {
        this.street = street;
        this.zipCode = zipCode;
        this.detail = detail;
    }
}
