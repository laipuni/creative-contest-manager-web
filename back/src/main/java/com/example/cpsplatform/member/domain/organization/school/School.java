package com.example.cpsplatform.member.domain.organization.school;

import com.example.cpsplatform.member.domain.organization.Organization;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "school")
public class School extends Organization {

    @Column(name = "school_type",length = 20,nullable = false)
    @Enumerated(EnumType.STRING)
    private StudentType studentType;

    @Column(nullable = false)
    private int grade;

    public School(final String name, final StudentType studentType, final int grade) {
        super(name);
        this.studentType = studentType;
        this.grade = grade;
    }

    @Override
    public String getOrganizationType() {
        return this.studentType.getDescription();
    }

    @Override
    public String getPosition() {
        return String.valueOf(this.grade);
    }

}
