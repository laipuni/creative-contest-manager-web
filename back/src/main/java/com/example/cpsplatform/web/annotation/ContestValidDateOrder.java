package com.example.cpsplatform.web.annotation;

import com.example.cpsplatform.web.validator.ContestDateOrderValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContestDateOrderValidator.class)
public @interface ContestValidDateOrder {

    String message() default "날짜 순서가 올바르지 않습니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String startRegistrationField() default "registrationStartAt";

    String endRegistrationField() default "registrationEndAt";

    String startContestField() default "contestStartAt";

    String endContestField() default "contestEndAt";

}
