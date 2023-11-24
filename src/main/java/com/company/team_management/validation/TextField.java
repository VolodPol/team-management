package com.company.team_management.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = TextFieldValidator.class)
public @interface TextField {
    String message() default "TextFiled.invalid\n Value must not be empty or null, up to 255 in length, simple text";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
