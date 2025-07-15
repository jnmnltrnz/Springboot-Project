package net.javaguides.springboot_backend.controller;

import net.javaguides.springboot_backend.entity.AuditTrail;
import net.javaguides.springboot_backend.payload.ApiResponse;
import net.javaguides.springboot_backend.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditTrailController {

    @Autowired
    private AuditService auditService;

    // Get all audit logs
    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditTrail>>> getAllAuditLogs() {
        List<AuditTrail> auditLogs = auditService.getAllAuditLogs();
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", auditLogs));
    }

    // Get audit logs by user
    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<List<AuditTrail>>> getAuditLogsByUser(@PathVariable String username) {
        List<AuditTrail> auditLogs = auditService.getAuditLogsByUser(username);
        return ResponseEntity.ok(ApiResponse.success("Audit logs by user retrieved successfully", auditLogs));
    }

    // (Optional) Add a manual audit log
    @PostMapping
    public ResponseEntity<ApiResponse<AuditTrail>> addAuditLog(@RequestBody AuditTrail auditTrail) {
        AuditTrail savedAuditLog = auditService.addManualAuditLog(auditTrail);
        return ResponseEntity.ok(ApiResponse.success("Audit log added successfully", savedAuditLog));
    }
}