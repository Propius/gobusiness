package com.govtech.scrabble.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

/**
 * Validator implementation for ValidWord annotation.
 * Validates that a word contains only alphabetic characters.
 */
public class WordValidator implements ConstraintValidator<ValidWord, String> {

    private static final String VALID_WORD_PATTERN = "^[A-Za-z]+$";

    @Override
    public void initialize(ValidWord constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String word, ConstraintValidatorContext context) {
        // Null or empty strings are handled by @NotBlank validation
        if (!StringUtils.hasText(word)) {
            return true;
        }

        // Check if word contains only alphabetic characters
        boolean isValidPattern = word.matches(VALID_WORD_PATTERN);
        
        if (!isValidPattern) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Word '" + sanitizeForMessage(word) + "' contains invalid characters. Only letters A-Z are allowed."
            ).addConstraintViolation();
            return false;
        }

        return true;
    }

    /**
     * Sanitizes the word for safe inclusion in error messages.
     * Removes any potentially harmful characters.
     */
    private String sanitizeForMessage(String word) {
        if (word == null) {
            return "null";
        }
        
        // Limit length and remove non-printable characters for security
        String sanitized = word.replaceAll("[\\p{Cntrl}\\p{Space}]", "")
                               .substring(0, Math.min(word.length(), 50));
        
        return sanitized.isEmpty() ? "[empty]" : sanitized;
    }
}