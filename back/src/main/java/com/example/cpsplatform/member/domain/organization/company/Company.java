package com.example.cpsplatform.member.domain.organization.company;

import com.example.cpsplatform.member.domain.organization.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;

@Getter
@DiscriminatorValue(value = "company")
public class Company extends Organization {

    @Column(nullable = false,length = 50)
    private String position;

    public Company(final String name, final String position) {
        super(name);
        this.position = position;
    }
}
