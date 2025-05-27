package com.example.cpsplatform.admin.analysis.response;

import com.example.cpsplatform.member.domain.organization.company.FieldType;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrganizationDistributionDto {

    private String organizationType;
    private String description;
    private Long count;

    @Builder
    public OrganizationDistributionDto(final String organizationType, final Long count) {
        this.organizationType = organizationType;
        this.count = count;
    }

    public void setDescription(){
        StudentType studentType = StudentType.findStudentTypeByKey(organizationType);
        FieldType fieldType = FieldType.findFiledTypeByKey(organizationType);
        if(studentType != null){
            this.description = studentType.getDescription();
        } else if(fieldType != null){
            this.description = fieldType.getDescription();
        }
    }
}
