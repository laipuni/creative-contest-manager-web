package com.example.cpsplatform.member.domain.organization.company;

import com.example.cpsplatform.member.domain.organization.Organization;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "company")
public class Company extends Organization {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private FieldType fieldType;

    @Column(nullable = false,length = 50)
    private String position;

    public Company(final String name, final String position, final FieldType fieldType) {
        super(name);
        this.position = position;
        this.fieldType = fieldType;
    }

    @Override
    public String getOrganizationType() {
        return fieldType.getDescription();
    }

    @Override
    public String getPosition() {
        return this.position;
    }
}
