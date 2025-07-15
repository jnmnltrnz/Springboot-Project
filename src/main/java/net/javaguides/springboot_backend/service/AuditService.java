package net.javaguides.springboot_backend.service;

import net.javaguides.springboot_backend.entity.AuditTrail;
import net.javaguides.springboot_backend.repositories.AuditTrailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditService {

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    public AuditTrail createAuditTrail(String actionMessage, String performedBy) {
        AuditTrail audit = new AuditTrail();
        audit.setActionMessage(actionMessage);
        audit.setDateTriggered(LocalDateTime.now());
        audit.setPerformedBy(performedBy);
        return auditTrailRepository.save(audit);
    }

    public List<AuditTrail> getAllAuditLogs() {
        return auditTrailRepository.findAll();
    }

    public List<AuditTrail> getAuditLogsByUser(String username) {
        return auditTrailRepository.findAll()
                .stream()
                .filter(audit -> username.equals(audit.getPerformedBy()))
                .toList();
    }

    public AuditTrail addManualAuditLog(AuditTrail auditTrail) {
        return auditTrailRepository.save(auditTrail);
    }
} 