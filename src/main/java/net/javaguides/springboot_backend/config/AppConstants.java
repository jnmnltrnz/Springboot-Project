package net.javaguides.springboot_backend.config;

public class AppConstants {
    
    // API Endpoints
    public static final String API_BASE_PATH = "/api";
    public static final String ACCOUNTS_PATH = API_BASE_PATH + "/accounts";
    public static final String EMPLOYEES_PATH = API_BASE_PATH + "/employees";
    public static final String PROJECTS_PATH = API_BASE_PATH + "/projects";
    public static final String MEETINGS_PATH = API_BASE_PATH + "/meetings";
    public static final String AUDIT_PATH = API_BASE_PATH + "/audit";
    
    // File Upload Constants
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String UPLOAD_DIR = "uploads";
    
    // Pagination Constants
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    
    // Validation Constants
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_PHONE_LENGTH = 20;
    
    // Business Logic Constants
    public static final int DEFAULT_PROJECT_PROGRESS = 0;
    public static final int DEFAULT_TEAM_SIZE = 1;
    public static final int MAX_TEAM_SIZE = 1000;
    public static final double MIN_SALARY = 0.0;
    public static final double MAX_SALARY = 1000000.0;
    
    // Session Constants
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    public static final String SESSION_HEADER = "X-Session-Id";
    
    // Audit Constants
    public static final int MAX_AUDIT_MESSAGE_LENGTH = 500;
    
    // Error Messages
    public static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    public static final String ERROR_INVALID_INPUT = "Invalid input provided";
    public static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    public static final String ERROR_FILE_TOO_LARGE = "File size exceeds maximum limit";
    public static final String ERROR_INVALID_FILE_TYPE = "Invalid file type";
    public static final String ERROR_DUPLICATE_ENTRY = "Duplicate entry found";
    
    // Success Messages
    public static final String SUCCESS_CREATED = "Resource created successfully";
    public static final String SUCCESS_UPDATED = "Resource updated successfully";
    public static final String SUCCESS_DELETED = "Resource deleted successfully";
    public static final String SUCCESS_LOGIN = "Login successful";
    public static final String SUCCESS_LOGOUT = "Logout successful";
    public static final String SUCCESS_FILE_UPLOADED = "File uploaded successfully";
    
    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm";
} 