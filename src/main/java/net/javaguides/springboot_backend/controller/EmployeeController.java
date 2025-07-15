package net.javaguides.springboot_backend.controller;

import net.javaguides.springboot_backend.entity.*;
import net.javaguides.springboot_backend.payload.ApiResponse;
import net.javaguides.springboot_backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import net.javaguides.springboot_backend.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<List<Employee>>> fetchEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employees));
    }

    @PostMapping("/addEmployee")
    public ResponseEntity<ApiResponse<Employee>> addEmployee(@RequestBody Employee employee, @RequestParam String username) {
        try {
            Employee savedEmployee = employeeService.createEmployee(employee, username);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Employee created successfully", savedEmployee));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create employee: " + e.getMessage()));
        }
    }


    @DeleteMapping("/deleteEmployee/{id}")
    public ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable Long id, @RequestParam String username, @RequestParam String employeeName) {
        try {
            employeeService.deleteEmployee(id, username, employeeName);
            return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Employee not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete employee: " + e.getMessage()));
        }
    }

    @PutMapping("/updateEmployee/{id}")
    public ResponseEntity<ApiResponse<Employee>> updateEmployee(@PathVariable Long id, @RequestBody Employee employee, @RequestParam String username) {
        Employee updated = employeeService.updateEmployee(id, employee, username);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", updated));
    }

    @PostMapping("/employees/{id}/upload")
    public ResponseEntity<ApiResponse<String>> uploadDocument(@PathVariable Long id, @RequestParam("file") MultipartFile file, @RequestParam String username) {
        try {
            String result = employeeService.uploadDocument(id, file, username);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("File upload failed: " + e.getMessage()));
        }
    }

    // Retrieve all documents for a specific employee
    @GetMapping("/employees/{id}/documents")
    public ResponseEntity<ApiResponse<List<Document>>> getDocumentsByEmployeeId(@PathVariable Long id) {
        List<Document> documents = employeeService.getDocumentsByEmployeeId(id);
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", documents));
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
        Document doc = employeeService.getDocumentById(documentId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + doc.getFileName() + "\"")
                .header("Content-Type", doc.getFileType())
                .body(doc.getData());
    }

    // Delete a document by its document id
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<ApiResponse<String>> deleteDocument(@PathVariable Long documentId, @RequestParam String username, @RequestParam String employeeName, @RequestParam String documentName) {
        employeeService.deleteDocument(documentId, username, employeeName, documentName);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }

    // ========== PROFILE IMAGE ENDPOINTS ==========

    @PostMapping("/employees/{id}/profile-image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfile(@PathVariable Long id, @RequestParam("profileImage") MultipartFile file) {
        try {
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile image uploaded successfully");
            response.put("imageUrl", "/api/employees/" + id + "/profile-image");
            
            return ResponseEntity.ok(ApiResponse.success("Profile image uploaded successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Profile upload failed: " + e.getMessage()));
        }
    }

    // Retrieve profile for a specific employee
    @GetMapping("/employees/{id}/profile-image")
    public ResponseEntity<?> getProfileEmployee(@PathVariable Long id) {
        try {
            ProfileEmployee profileEmployee = employeeService.getProfileImage(id);
            if (profileEmployee == null || profileEmployee.getData() == null) {
                return ResponseEntity.ok().body(Map.of("imageUrl", null));
            }

            // Return the actual image data directly
            return ResponseEntity.ok()
                    .header("Content-Type", profileEmployee.getFileType())
                    .header("Cache-Control", "public, max-age=31536000") // Cache for 1 year
                    .body(profileEmployee.getData());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Employee not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve profile image: " + e.getMessage()));
        }
    }

    // Download profile image
    @GetMapping("/employees/{id}/profile-image/download")
    public ResponseEntity<byte[]> downloadProfileImage(@PathVariable Long id) {
        ProfileEmployee profile = employeeService.getProfileImage(id);
        if (profile == null || profile.getData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + profile.getFileName() + "\"")
                .header("Content-Type", profile.getFileType())
                .body(profile.getData());
    }

    // Delete profile image
    @DeleteMapping("/employees/{id}/profile-image")
    public ResponseEntity<ApiResponse<String>> deleteProfileImage(@PathVariable Long id) {
        employeeService.deleteProfileImage(id);
        return ResponseEntity.ok(ApiResponse.success("Profile image deleted successfully", null));
    }
}