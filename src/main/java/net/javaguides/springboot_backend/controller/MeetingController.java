package net.javaguides.springboot_backend.controller;

import net.javaguides.springboot_backend.entity.Meeting;
import net.javaguides.springboot_backend.entity.Employee;
import net.javaguides.springboot_backend.entity.AuditTrail;
import net.javaguides.springboot_backend.repositories.MeetingRepository;
import net.javaguides.springboot_backend.repositories.EmployeeRepository;
import net.javaguides.springboot_backend.repositories.AuditTrailRepository;
import net.javaguides.springboot_backend.status.MeetingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    // Get all meetings
    @GetMapping("/meetings")
    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    // Get upcoming meetings
    @GetMapping("/meetings/upcoming")
    public List<Meeting> getUpcomingMeetings() {
        return meetingRepository.findUpcomingMeetings(LocalDate.now());
    }

    // Get meetings by date
    @GetMapping("/meetings/date/{date}")
    public List<Meeting> getMeetingsByDate(@PathVariable String date) {
        LocalDate meetingDate = LocalDate.parse(date);
        return meetingRepository.findByMeetingDateOrderByMeetingTimeAsc(meetingDate);
    }

    // Get meeting by ID
    @GetMapping("/meetings/{id}")
    public ResponseEntity<Meeting> getMeetingById(@PathVariable Long id) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        return meeting.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new meeting
    @PostMapping("/meetings")
    public ResponseEntity<Meeting> createMeeting(@RequestBody MeetingRequest meetingRequest, @RequestParam String username) {
        try {
            // Parse date and time from strings
            LocalDate meetingDate = LocalDate.parse(meetingRequest.getDate());
            LocalTime meetingTime = LocalTime.parse(meetingRequest.getTime());

            // Get employee invitees
            List<Employee> invitees = employeeRepository.findAllById(meetingRequest.getInviteeIds());

            Meeting meeting = Meeting.builder()
                    .title(meetingRequest.getTitle())
                    .meetingDate(meetingDate)
                    .meetingTime(meetingTime)
                    .createdAt(LocalDateTime.now())
                    .createdBy(username)
                    .invitees(invitees)
                    .status(MeetingStatus.SCHEDULED)
                    .notes(meetingRequest.getNotes())
                    .build();

            Meeting savedMeeting = meetingRepository.save(meeting);

            // Audit trail
            AuditTrail audit = new AuditTrail();
            audit.setActionMessage("Created meeting: " + savedMeeting.getTitle());
            audit.setDateTriggered(LocalDateTime.now());
            audit.setPerformedBy(username);
            auditTrailRepository.save(audit);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedMeeting);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update meeting
    @PutMapping("/meetings/{id}")
    public ResponseEntity<Meeting> updateMeeting(@PathVariable Long id, @RequestBody MeetingRequest meetingRequest, @RequestParam String username) {
        Optional<Meeting> existingMeeting = meetingRepository.findById(id);
        if (existingMeeting.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Meeting meeting = existingMeeting.get();
            meeting.setTitle(meetingRequest.getTitle());
            meeting.setMeetingDate(LocalDate.parse(meetingRequest.getDate()));
            meeting.setMeetingTime(LocalTime.parse(meetingRequest.getTime()));
            meeting.setNotes(meetingRequest.getNotes());

            if (meetingRequest.getInviteeIds() != null) {
                List<Employee> invitees = employeeRepository.findAllById(meetingRequest.getInviteeIds());
                meeting.setInvitees(invitees);
            }

            Meeting updatedMeeting = meetingRepository.save(meeting);

            // Audit trail
            AuditTrail audit = new AuditTrail();
            audit.setActionMessage("Updated meeting: " + updatedMeeting.getTitle());
            audit.setDateTriggered(LocalDateTime.now());
            audit.setPerformedBy(username);
            auditTrailRepository.save(audit);

            return ResponseEntity.ok(updatedMeeting);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update meeting status
    @PatchMapping("/meetings/{id}/status")
    public ResponseEntity<Meeting> updateMeetingStatus(@PathVariable Long id, @RequestParam MeetingStatus status, @RequestParam String username) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        if (meeting.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Meeting existingMeeting = meeting.get();
        existingMeeting.setStatus(status);
        Meeting updatedMeeting = meetingRepository.save(existingMeeting);

        // Audit trail
        AuditTrail audit = new AuditTrail();
        audit.setActionMessage("Updated meeting status: " + updatedMeeting.getTitle() + " to " + status.getDisplayName());
        audit.setDateTriggered(LocalDateTime.now());
        audit.setPerformedBy(username);
        auditTrailRepository.save(audit);

        return ResponseEntity.ok(updatedMeeting);
    }

    // Delete meeting
    @DeleteMapping("/meetings/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id, @RequestParam String username) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        if (meeting.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String meetingTitle = meeting.get().getTitle();
        meetingRepository.deleteById(id);

        // Audit trail
        AuditTrail audit = new AuditTrail();
        audit.setActionMessage("Deleted meeting: " + meetingTitle);
        audit.setDateTriggered(LocalDateTime.now());
        audit.setPerformedBy(username);
        auditTrailRepository.save(audit);

        return ResponseEntity.noContent().build();
    }

    // Get meetings by creator
    @GetMapping("/meetings/created-by/{username}")
    public List<Meeting> getMeetingsByCreator(@PathVariable String username) {
        return meetingRepository.findByCreatedByOrderByCreatedAtDesc(username);
    }

    // Get meetings by status
    @GetMapping("/meetings/status/{status}")
    public List<Meeting> getMeetingsByStatus(@PathVariable MeetingStatus status) {
        return meetingRepository.findByStatus(status);
    }

    // Inner class for meeting request
    public static class MeetingRequest {
        private String title;
        private String date;
        private String time;
        private List<Long> inviteeIds;
        private String notes;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }

        public List<Long> getInviteeIds() { return inviteeIds; }
        public void setInviteeIds(List<Long> inviteeIds) { this.inviteeIds = inviteeIds; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
} 