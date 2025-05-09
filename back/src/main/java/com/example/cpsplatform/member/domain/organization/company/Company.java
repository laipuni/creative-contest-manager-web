package com.example.cpsplatform.member.domain.organization.company;

import com.example.cpsplatform.member.domain.organization.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "company")
public class Company extends Organization {

    @Column(nullable = false,length = 50)
    private String position;

    public Company(final String name, final String position) {
        super(name);
        this.position = position;
    }

    @Override
    public String getOrganizationType() {
        return "직장인";
    }

    @Override
    public String getPosition() {
        return this.position;
    }
}
