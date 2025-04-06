package com.example.cpsplatform.member.controller.request;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrganizationValidator.class)
public @interface ValidOrganization {

    String message() default "잘못된 입력값을 받았습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
