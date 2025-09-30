package com.govtech.scrabble.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for validating Scrabble words.
 * Ensures the word contains only alphabetic characters.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WordValidator.class)
public @interface ValidWord {
    String message() default "Word must contain only alphabetic characters (A-Z)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}