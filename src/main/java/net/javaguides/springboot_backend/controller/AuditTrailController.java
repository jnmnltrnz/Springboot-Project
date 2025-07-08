package net.javaguides.springboot_backend.controller;

import net.javaguides.springboot_backend.entity.AuditTrail;
import net.javaguides.springboot_backend.repositories.AuditTrailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditTrailController {

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    // Get all audit logs
    @GetMapping
    public List<AuditTrail> getAllAuditLogs() {
        return auditTrailRepository.findAll();
    }

    // Get audit logs by user
    @GetMapping("/user/{username}")
    public List<AuditTrail> getAuditLogsByUser(@PathVariable String username) {
        return auditTrailRepository.findAll()
                .stream()
                .filter(audit -> username.equals(audit.getPerformedBy()))
                .toList();
    }

    // (Optional) Add a manual audit log
    @PostMapping
    public AuditTrail addAuditLog(@RequestBody AuditTrail auditTrail) {
        return auditTrailRepository.save(auditTrail);
    }
}