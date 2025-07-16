package net.javaguides.springboot_backend.service;

import net.javaguides.springboot_backend.entity.Employee;
import net.javaguides.springboot_backend.entity.Meeting;
import net.javaguides.springboot_backend.exception.ResourceNotFoundException;
import net.javaguides.springboot_backend.payload.MeetingRequest;
import net.javaguides.springboot_backend.repositories.EmployeeRepository;
import net.javaguides.springboot_backend.repositories.MeetingRepository;
import net.javaguides.springboot_backend.status.MeetingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

@Service
@Transactional
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AuditService auditService;

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAllWithInvitees();
    }

    public List<Meeting> getUpcomingMeetings() {
        return meetingRepository.findUpcomingMeetings(LocalDate.now());
    }

    public List<Meeting> getMeetingsByDate(String date) {
        LocalDate meetingDate = LocalDate.parse(date);
        return meetingRepository.findByMeetingDateOrderByMeetingTimeAsc(meetingDate);
    }

    public Meeting getMeetingById(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found with id: " + id));
    }

    public Meeting createMeeting(MeetingRequest meetingRequest, String username) {
        // Validate required fields
        if (meetingRequest.getTitle() == null || meetingRequest.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Meeting title is required");
        }
        if (meetingRequest.getDate() == null) {
            throw new IllegalArgumentException("Meeting date is required");
        }
        if (meetingRequest.getTime() == null ) {
            throw new IllegalArgumentException("Meeting time is required");
        }
        try {
          meetingRequest.getDate();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
        }
        try {
          meetingRequest.getTime();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format. Expected HH:mm");
        }
        // Parse date and time from strings
        LocalDate meetingDate = meetingRequest.getDate();
        LocalTime meetingTime = meetingRequest.getTime();

        // Create meeting without invitees first
        Meeting meeting = Meeting.builder()
                .title(meetingRequest.getTitle())
                .meetingDate(meetingDate)
                .meetingTime(meetingTime)
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .status(MeetingStatus.SCHEDULED)
                .notes(meetingRequest.getNotes())
                .build();

        // Set invitees if provided
        if (meetingRequest.getInviteeIds() != null && !meetingRequest.getInviteeIds().isEmpty()) {
            List<Employee> invitees = employeeRepository.findAllById(meetingRequest.getInviteeIds());
            meeting.setInvitees(invitees);
        }

        Meeting savedMeeting = meetingRepository.save(meeting);

        // Audit trail
        auditService.createAuditTrail("Created meeting: " + savedMeeting.getTitle(), username);

        return savedMeeting;
    }

    public Meeting updateMeeting(Long id, MeetingRequest meetingRequest, String username) {
        Meeting meeting = getMeetingById(id);
        
        meeting.setTitle(meetingRequest.getTitle());
        meeting.setMeetingDate(meetingRequest.getDate());
        meeting.setMeetingTime(meetingRequest.getTime());
        meeting.setNotes(meetingRequest.getNotes());

        // Update invitees if provided
        if (meetingRequest.getInviteeIds() != null) {
            if (meetingRequest.getInviteeIds().isEmpty()) {
                meeting.setInvitees(new ArrayList<>());
            } else {
                List<Employee> invitees = employeeRepository.findAllById(meetingRequest.getInviteeIds());
                meeting.setInvitees(invitees);
            }
        }

        Meeting updatedMeeting = meetingRepository.save(meeting);

        // Audit trail
        auditService.createAuditTrail("Updated meeting: " + updatedMeeting.getTitle(), username);

        return updatedMeeting;
    }

    public Meeting updateMeetingStatus(Long id, MeetingStatus status, String username) {
        Meeting meeting = getMeetingById(id);
        meeting.setStatus(status);
        Meeting updatedMeeting = meetingRepository.save(meeting);

        // Audit trail
        auditService.createAuditTrail("Updated meeting status: " + updatedMeeting.getTitle() + " to " + status.getDisplayName(), username);

        return updatedMeeting;
    }

    public void deleteMeeting(Long id, String username) {
        Meeting meeting = getMeetingById(id);
        String meetingTitle = meeting.getTitle();
        
        // Delete the meeting
        meetingRepository.deleteById(id);

        // Audit trail
        auditService.createAuditTrail("Deleted meeting: " + meetingTitle, username);
    }

    public List<Meeting> getMeetingsByCreator(String username) {
        return meetingRepository.findByCreatedByOrderByCreatedAtDesc(username);
    }

    public List<Meeting> getMeetingsByStatus(MeetingStatus status) {
        return meetingRepository.findByStatus(status);
    }

    public List<Meeting> getMeetingsByEmployeeId(Long employeeId) {
        return meetingRepository.findMeetingsByEmployeeId(employeeId);
    }




} 