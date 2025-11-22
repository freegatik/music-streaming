package ru.music.streaming.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<StrongPassword, String> {
    
    @Override
    public void initialize(StrongPassword constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return false;
        }
        
        // Минимум 8 символов
        if (password.length() < 8) {
            return false;
        }
        
        // Хотя бы одна цифра
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        if (!hasDigit) {
            return false;
        }
        
        // Хотя бы одна буква (верхний или нижний регистр)
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        if (!hasLetter) {
            return false;
        }
        
        // Хотя бы один специальный символ
        boolean hasSpecialChar = password.chars()
                .anyMatch(ch -> !Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch));
        if (!hasSpecialChar) {
            return false;
        }
        
        return true;
    }
}

