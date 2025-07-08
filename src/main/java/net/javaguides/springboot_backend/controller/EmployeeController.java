package net.javaguides.springboot_backend.controller;

import net.javaguides.springboot_backend.entity.*;
import net.javaguides.springboot_backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.RandomStringUtils;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AuditTrailRepository auditTrailRepository;


    @GetMapping("/employees")
    public List<Employee> fetchEmployees() {
        return employeeRepository.findByFirstNameNot("admin");

    }

    @PostMapping("/addEmployee")
    public Employee addEmployee(@RequestBody Employee employee, @RequestParam String username) {
        String rawPassword = RandomStringUtils.randomAlphanumeric(10);

        // Create account
        Account account = new Account();
        account.setUsername(employee.getFirstName().toLowerCase() + "." + employee.getLastName().toLowerCase());
        account.setPassword(rawPassword);
        account.setAuthenticated(false);
        employee.setAccount(account);
        employee.setCreatedAt(LocalDateTime.now());

        // Save Employee (and Account)
        Employee savedEmployee = employeeRepository.save(employee);

        // Create and save default profile
        ProfileEmployee profileEmployee = new ProfileEmployee();
        profileEmployee.setEmployee(savedEmployee);
        profileEmployee.setFileName(null);
        profileEmployee.setFileType(null);
        profileEmployee.setData(null);
        profileRepository.save(profileEmployee);

        // --- AUDIT TRAIL ---
        AuditTrail audit = new AuditTrail();
        audit.setActionMessage("New employee " + savedEmployee.getFirstName() + " " + savedEmployee.getLastName() + " was added");
        audit.setDateTriggered(LocalDateTime.now());
        audit.setPerformedBy(username);
        auditTrailRepository.save(audit);

        return savedEmployee;
    }


    @DeleteMapping("/deleteEmployee/{id}")
    public void deleteEmployee(@PathVariable Long id, @RequestParam String username, @RequestParam String employeeName) {
        // Fetch all documents associated with the employee
        List<Document> documents = documentRepository.findByEmployeeId(id);
        ProfileEmployee profileEmployeeObject = profileRepository.findByEmployeeId(id);

        // If there are documents, delete them
        if (!documents.isEmpty()) {
            documentRepository.deleteAll(documents);
        }

        if (profileEmployeeObject != null) {
            profileRepository.delete(profileEmployeeObject);
        }

        // Delete profile image if exists
        ProfileEmployee profile = profileRepository.findByEmployeeId(id);
        if (profile != null) {
            profileRepository.delete(profile);
        }

        AuditTrail audit = new AuditTrail();
        audit.setActionMessage("Deleted employee " + employeeName);
        audit.setDateTriggered(LocalDateTime.now());
        audit.setPerformedBy(username);
        auditTrailRepository.save(audit);

        // Delete the employee
        employeeRepository.deleteById(id);
    }

    @PutMapping("/updateEmployee/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee, @RequestParam String username) {
        employee.setId(id);
        Employee updated = employeeRepository.save(employee);

        // --- AUDIT TRAIL ---
        AuditTrail audit = new AuditTrail();
        audit.setActionMessage("Updated employee " + updated.getFirstName() + " " + updated.getLastName());
        audit.setDateTriggered(LocalDateTime.now());
        audit.setPerformedBy(username);
        auditTrailRepository.save(audit);

        return updated;
    }

    @PostMapping("/employees/{id}/upload")
    public ResponseEntity<String> uploadDocument(@PathVariable Long id, @RequestParam("file") MultipartFile file, @RequestParam String username) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        // 1. Find the employee
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }
        Employee employee = employeeOpt.get();

        try {
            // 2. Save document info and file data to DB
            Document document = Document.builder().fileName(file.getOriginalFilename()).fileType(file.getContentType()).data(file.getBytes()).employee(employee).build();
            documentRepository.save(document);

            // --- AUDIT TRAIL ---
            AuditTrail audit = new AuditTrail();
            audit.setActionMessage("Uploaded document for " + employee.getFirstName().toLowerCase() + ": " + file.getOriginalFilename());
            audit.setDateTriggered(LocalDateTime.now());
            audit.setPerformedBy(username);
            auditTrailRepository.save(audit);

            return ResponseEntity.ok("File uploaded and saved to DB: " + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    // Retrieve all documents for a specific employee
    @GetMapping("/employees/{id}/documents")
    public ResponseEntity<List<Document>> getDocumentsByEmployeeId(@PathVariable Long id) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Document> documents = documentRepository.findByEmployeeId(id);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
        Optional<Document> docOpt = documentRepository.findById(documentId);
        if (docOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Document doc = docOpt.get();
        return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"" + doc.getFileName() + "\"").header("Content-Type", doc.getFileType()).body(doc.getData());
    }

    // Delete a document by its document id
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId, @RequestParam String username, @RequestParam String employeeName, @RequestParam String documentName) {
        if (!documentRepository.existsById(documentId)) {
            return ResponseEntity.notFound().build();
        }
        documentRepository.deleteById(documentId);

        // --- AUDIT TRAIL ---
        AuditTrail audit = new AuditTrail();
        audit.setActionMessage("Deleted " + documentName + " for " + employeeName);
        audit.setDateTriggered(LocalDateTime.now());
        audit.setPerformedBy(username);
        auditTrailRepository.save(audit);


        return ResponseEntity.noContent().build();
    }

    // ========== PROFILE IMAGE ENDPOINTS ==========

    @PostMapping("/employees/{id}/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfile(@PathVariable Long id, @RequestParam("profileImage") MultipartFile file) {

        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "No file uploaded");
            return ResponseEntity.badRequest().body(response);
        }

        // Validate file type
        if (!file.getContentType().startsWith("image/")) {
            response.put("error", "Only image files are allowed");
            return ResponseEntity.badRequest().body(response);
        }

        // 1. Find the employee
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            response.put("error", "Employee not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Employee employee = employeeOpt.get();

        try {
            // Check if employee already has a profile image and delete it
            ProfileEmployee existingProfile = profileRepository.findByEmployeeId(id);
            if (existingProfile != null) {
                profileRepository.delete(existingProfile);
            }

            // 2. Save profile info and file data to DB
            ProfileEmployee profileEmployee = ProfileEmployee.builder().fileName(file.getOriginalFilename()).fileType(file.getContentType()).data(file.getBytes()).employee(employee).build();
            profileRepository.save(profileEmployee);

            response.put("message", "Profile image uploaded successfully");
            response.put("imageUrl", "/api/employees/" + id + "/profile-image");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("error", "Profile upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Retrieve profile for a specific employee
    @GetMapping("/employees/{id}/profile-image")
    public ResponseEntity<?> getProfileEmployee(@PathVariable Long id) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ProfileEmployee profileEmployee = profileRepository.findByEmployeeId(id);
        if (profileEmployee == null) {
            return ResponseEntity.ok().body(Map.of("imageUrl", null));
        }

        // Return the actual image data directly
        return ResponseEntity.ok()
                .header("Content-Type", profileEmployee.getFileType())
                .header("Cache-Control", "public, max-age=31536000") // Cache for 1 year
                .body(profileEmployee.getData());
    }

    // Download profile image
    @GetMapping("/employees/{id}/profile-image/download")
    public ResponseEntity<byte[]> downloadProfileImage(@PathVariable Long id) {
        ProfileEmployee profile = profileRepository.findByEmployeeId(id);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + profile.getFileName() + "\"").header("Content-Type", profile.getFileType()).body(profile.getData());
    }

    // Delete profile image
    @DeleteMapping("/employees/{id}/profile-image")
    public ResponseEntity<Void> deleteProfileImage(@PathVariable Long id) {
        ProfileEmployee profile = profileRepository.findByEmployeeId(id);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        profileRepository.delete(profile);
        return ResponseEntity.noContent().build();
    }
}