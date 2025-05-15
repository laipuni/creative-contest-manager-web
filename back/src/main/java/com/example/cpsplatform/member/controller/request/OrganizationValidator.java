package com.example.cpsplatform.member.controller.request;

import com.example.cpsplatform.member.domain.organization.school.StudentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OrganizationValidator implements ConstraintValidator<ValidOrganization, OrganizationRequest> {

    private static final String INVALID_GRADE = "학년은 숫자만 입력해주세요.";

    @Override
    public boolean isValid(final OrganizationRequest value, final ConstraintValidatorContext context) {
        StudentType studentType = StudentType.findStudentTypeBy(value.getOrganizationType());

        if (studentType == null) {
            return true; // 학생이 아닐 경우 검증 패스
        }

        Integer grade = parseGrade(value.getPosition(), context);
        if (grade == null) {
            return false; // 숫자 변환 실패 시 검증 실패
        }

        return validateGrade(studentType, grade, context);
    }

    private Integer parseGrade(String position, ConstraintValidatorContext context) {
        try {
            return Integer.parseInt(position);
        } catch (NumberFormatException e) {
            setContextConstraint(context, INVALID_GRADE);
            return null;
        }
    }

    private boolean validateGrade(StudentType studentType, int grade, ConstraintValidatorContext context) {
        int maxGrade = switch (studentType) {
            case ELEMENTARY -> 6;
            case MIDDLE, HIGH -> 3;
            case COLLEGE -> 4;
        };

        if (!isValidGrade(grade, maxGrade)) {
            setContextConstraint(context, studentType.getDescription() + "은 1부터 " + maxGrade + "까지 입력 가능합니다.");
            return false;
        }
        return true;
    }

    private static boolean isValidGrade(int grade, int max) {
        return grade >= 1 && grade <= max;
    }

    private static void setContextConstraint(final ConstraintValidatorContext context, final String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("position")
                .addConstraintViolation();
    }
}
