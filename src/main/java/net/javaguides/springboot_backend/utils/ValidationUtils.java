package net.javaguides.springboot_backend.utils;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-\\(\\)]{10,}$");
    
    public static boolean isValidEmail(String email) {
        return StringUtils.hasText(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return StringUtils.hasText(phone) && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidUsername(String username) {
        return StringUtils.hasText(username) && username.length() >= 3 && username.length() <= 50;
    }
    
    public static boolean isValidPassword(String password) {
        return StringUtils.hasText(password) && password.length() >= 6;
    }
    
    public static boolean isValidName(String name) {
        return StringUtils.hasText(name) && name.length() >= 2 && name.length() <= 100;
    }
    
    public static boolean isValidDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now().plusYears(10));
    }
    
    public static boolean isValidDateTime(LocalDateTime dateTime) {
        return dateTime != null && !dateTime.isAfter(LocalDateTime.now().plusYears(10));
    }
    
    public static boolean isValidProgress(Integer progress) {
        return progress != null && progress >= 0 && progress <= 100;
    }
    
    public static boolean isValidTeamSize(Integer teamSize) {
        return teamSize != null && teamSize > 0 && teamSize <= 1000;
    }
    
    public static boolean isValidSalary(Double salary) {
        return salary != null && salary >= 0 && salary <= 1000000;
    }
    
    public static String sanitizeString(String input) {
        if (!StringUtils.hasText(input)) {
            return null;
        }
        return input.trim();
    }
    
    public static boolean isNullOrEmpty(String value) {
        return !StringUtils.hasText(value);
    }
    
    public static boolean isNotNullOrEmpty(String value) {
        return StringUtils.hasText(value);
    }
} 