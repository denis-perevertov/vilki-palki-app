package com.example.vilkipalki.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String number, ConstraintValidatorContext cxt) {
        return number != null && number.matches("\\+380[0-9]{9}");
    }
}
