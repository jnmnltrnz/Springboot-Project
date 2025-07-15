package net.javaguides.springboot_backend.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
        "application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain", "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    public static boolean isValidImageFile(MultipartFile file) {
        return file != null && 
               !file.isEmpty() && 
               ALLOWED_IMAGE_TYPES.contains(file.getContentType()) &&
               file.getSize() <= MAX_FILE_SIZE;
    }
    
    public static boolean isValidDocumentFile(MultipartFile file) {
        return file != null && 
               !file.isEmpty() && 
               ALLOWED_DOCUMENT_TYPES.contains(file.getContentType()) &&
               file.getSize() <= MAX_FILE_SIZE;
    }
    
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
    
    public static String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String nameWithoutExtension = getFileNameWithoutExtension(originalFileName);
        long timestamp = System.currentTimeMillis();
        return nameWithoutExtension + "_" + timestamp + "." + extension;
    }
    
    public static byte[] getFileBytes(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is null or empty");
        }
        return file.getBytes();
    }
    
    public static boolean isFileSizeValid(MultipartFile file, long maxSize) {
        return file != null && file.getSize() <= maxSize;
    }
    
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
} 