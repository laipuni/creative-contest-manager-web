package com.example.cpsplatform.web.validator;

import com.example.cpsplatform.web.annotation.ContestValidDateOrder;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class ContestDateOrderValidator implements ConstraintValidator<ContestValidDateOrder, Object> {

    private String startRegistrationField;
    private String endRegistrationField;
    private String startContestField;
    private String endContestField;

    @Override
    public void initialize(final ContestValidDateOrder constraintAnnotation) {
        this.startRegistrationField = constraintAnnotation.startRegistrationField();
        this.endRegistrationField = constraintAnnotation.endRegistrationField();
        this.startContestField = constraintAnnotation.startContestField();
        this.endContestField = constraintAnnotation.endContestField();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
            LocalDateTime registrationStart = (LocalDateTime) getFieldValue(value, startRegistrationField);
            LocalDateTime registrationEnd = (LocalDateTime) getFieldValue(value, endRegistrationField);
            LocalDateTime contestStart = (LocalDateTime) getFieldValue(value, startContestField);
            LocalDateTime contestEnd = (LocalDateTime) getFieldValue(value, endContestField);

            boolean valid = true;

            if (registrationStart != null && registrationEnd != null && !registrationStart.isBefore(registrationEnd)) {
                //예선 접수 시작 시간은 마감 시간보다 이전일 경우
                setContextConstraint(context,"예선 접수 시작 시간은 마감 시간보다 이전이어야 합니다.",startRegistrationField);
                valid = false;
            }

            if (contestStart != null && contestEnd != null && !contestStart.isBefore(contestEnd)) {
                //대회 시작 시간은 종료 시간보다 이전일 경우
                setContextConstraint(context,"대회 시작 시간은 종료 시간보다 이전이어야 합니다.",startContestField);
                valid = false;
            }

            return valid;
    }

    private void setContextConstraint(final ConstraintValidatorContext context, final String message, final String name) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(name)
                .addConstraintViolation();
    }

    private Object getFieldValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

}
