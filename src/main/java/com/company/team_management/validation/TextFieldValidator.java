package com.company.team_management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class TextFieldValidator implements ConstraintValidator<TextField, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty() || value.length() > 255)
            return false;

        Pattern pattern = Pattern.compile("^[A-Z\\d][\\w\\s,.?!\\-—()\"']+[.?!]$");
        return pattern.matcher(value)
                .matches();
    }
}
