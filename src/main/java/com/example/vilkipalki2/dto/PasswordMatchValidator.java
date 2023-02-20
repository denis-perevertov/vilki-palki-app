package com.example.vilkipalki2.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(final PasswordMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
        message = constraintAnnotation.message();

        System.out.println(firstFieldName);
        System.out.println(secondFieldName);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println("password validator start");

        boolean valid = true;
        try
        {
            String pass = "";
            String confirm = "";
            Field[] fields = o.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                if(field.getName().equals(firstFieldName)) pass = field.get(o).toString();
                if(field.getName().equals(secondFieldName)) confirm = field.get(o).toString();
            }

            System.out.println("pass: " + pass);
            System.out.println("confirm: " + confirm);

            valid =  pass == null && confirm == null || pass != null && pass.equals(confirm);

            System.out.println(valid);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }

        if (!valid){
            constraintValidatorContext.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(firstFieldName)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
    }
}
