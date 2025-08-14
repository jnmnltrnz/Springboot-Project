package net.javaguides.springboot_backend.service;

import net.javaguides.springboot_backend.entity.*;
import net.javaguides.springboot_backend.exception.ResourceNotFoundException;
import net.javaguides.springboot_backend.payload.DocumentResponse;
import net.javaguides.springboot_backend.repositories.*;
import net.javaguides.springboot_backend.utils.RandomAlpaNumUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.security.SecureRandom;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RandomAlpaNumUtils rAlpaNumUtils;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findByFirstNameNot("admin");
    }

    public Employee createEmployee(Employee employee, String username) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Employee with email " + employee.getEmail() + " already exists");
        }

        String rawPassword = rAlpaNumUtils.generateRandomAlphanumeric(10);

        // Create account with unique username
        Account account = new Account();
        String baseUsername = employee.getFirstName().toLowerCase() + "." + employee.getLastName().toLowerCase();
        String uniqueUsername = generateUniqueUsername(baseUsername);
        account.setUsername(uniqueUsername);
        account.setPassword(rawPassword);
        account.setDefaultPassword(true);
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

        // Audit trail
        auditService.createAuditTrail(
                "New employee " + savedEmployee.getFirstName() + " " + savedEmployee.getLastName() + " was added",
                username);

        return savedEmployee;
    }

    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;

        // Check if username exists and generate a unique one
        while (accountRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    public void deleteEmployee(Long id, String username, String employeeName) {
        try {
            // First, check if employee exists
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

            // Fetch all documents associated with the employee
            List<Document> documents = documentRepository.findByEmployeeId(id);

            // Delete documents if they exist
            if (!documents.isEmpty()) {
                documentRepository.deleteAll(documents);
            }

            // Delete profile image if exists
            ProfileEmployee profile = profileRepository.findByEmployeeId(id);
            if (profile != null) {
                profileRepository.delete(profile);
            }

            // Remove employee from all projects (this handles the project_employees table)
            // We need to do this manually since there's no cascade
            List<Project> projects = projectRepository.findAll();
            for (Project project : projects) {
                if (project.getAssignedEmployees() != null && project.getAssignedEmployees().contains(employee)) {
                    project.removeEmployee(employee);
                    projectRepository.save(project);
                }
            }

            // Remove employee from all meetings (this handles the meeting_invitees table)
            // We need to do this manually since there's no cascade
            List<Meeting> meetings = meetingRepository.findAll();
            for (Meeting meeting : meetings) {
                if (meeting.getInvitees() != null && meeting.getInvitees().contains(employee)) {
                    meeting.getInvitees().remove(employee);
                    meetingRepository.save(meeting);
                }
            }

            // Create audit trail before deleting employee
            auditService.createAuditTrail("Deleted employee " + employeeName, username);

            // Delete the employee (this will cascade to account due to @OneToOne cascade)
            employeeRepository.deleteById(id);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete employee: " + e.getMessage(), e);
        }
    }

    public Employee updateEmployee(Long id, Employee employee, String username) {
        employee.setId(id);
        Employee updated = employeeRepository.save(employee);

        // Audit trail
        auditService.createAuditTrail("Updated employee " + updated.getFirstName() + " " + updated.getLastName(),
                username);

        return updated;
    }

    public String uploadDocument(Long employeeId, MultipartFile file, String username) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded");
        }

        // Find the employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Save document info and file data to DB
        Document document = Document.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .data(file.getBytes())
                .employee(employee)
                .build();
        documentRepository.save(document);

        // Audit trail
        auditService.createAuditTrail(
                "Uploaded document for " + employee.getFirstName().toLowerCase() + ": " + file.getOriginalFilename(),
                username);

        return "File uploaded and saved to DB: " + file.getOriginalFilename();
    }

    public List<DocumentResponse> getDocumentResponsesByEmployeeId(Long employeeId) {
        List<Document> documents = documentRepository.findByEmployeeId(employeeId);
        return documents.stream().map(this::toDocumentResponse).toList();
    }

    private DocumentResponse toDocumentResponse(Document doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .fileName(doc.getFileName())
                .fileType(doc.getFileType())
                .build();
    }

    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
    }

    public void deleteDocument(Long documentId, String username, String employeeName, String documentName) {
        if (!documentRepository.existsById(documentId)) {
            throw new ResourceNotFoundException("Document not found");
        }
        documentRepository.deleteById(documentId);

        // Audit trail
        auditService.createAuditTrail("Deleted " + documentName + " for " + employeeName, username);
    }

    public ProfileEmployee uploadProfileImage(Long employeeId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Check if profile already exists
        ProfileEmployee existingProfile = profileRepository.findByEmployeeId(employeeId);
        if (existingProfile != null) {
            profileRepository.delete(existingProfile);
        }

        // Create new profile
        ProfileEmployee profileEmployee = new ProfileEmployee();
        profileEmployee.setEmployee(employee);
        profileEmployee.setFileName(file.getOriginalFilename());
        profileEmployee.setFileType(file.getContentType());
        profileEmployee.setData(file.getBytes());

        return profileRepository.save(profileEmployee);
    }

    public ProfileEmployee getProfileImage(Long employeeId) {
        // First check if employee exists
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        return profileRepository.findByEmployeeId(employeeId);
    }

    public void deleteProfileImage(Long employeeId) {
        ProfileEmployee profile = profileRepository.findByEmployeeId(employeeId);
        if (profile != null) {
            profileRepository.delete(profile);
        }
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

}