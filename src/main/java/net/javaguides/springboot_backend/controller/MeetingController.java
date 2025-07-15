package net.javaguides.springboot_backend.controller;

import net.javaguides.springboot_backend.entity.Meeting;
import net.javaguides.springboot_backend.payload.ApiResponse;
import net.javaguides.springboot_backend.payload.MeetingRequest;
import net.javaguides.springboot_backend.service.MeetingService;
import net.javaguides.springboot_backend.status.MeetingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    // Get all meetings
    @GetMapping("/meetings")
    public ResponseEntity<ApiResponse<List<Meeting>>> getAllMeetings() {
        List<Meeting> meetings = meetingService.getAllMeetings();
        return ResponseEntity.ok(ApiResponse.success("Meetings retrieved successfully", meetings));
    }

    // Get upcoming meetings
    @GetMapping("/meetings/upcoming")
    public ResponseEntity<ApiResponse<List<Meeting>>> getUpcomingMeetings() {
        List<Meeting> meetings = meetingService.getUpcomingMeetings();
        return ResponseEntity.ok(ApiResponse.success("Upcoming meetings retrieved successfully", meetings));
    }

    // Get meetings by date
    @GetMapping("/meetings/date/{date}")
    public ResponseEntity<ApiResponse<List<Meeting>>> getMeetingsByDate(@PathVariable String date) {
        List<Meeting> meetings = meetingService.getMeetingsByDate(date);
        return ResponseEntity.ok(ApiResponse.success("Meetings for date retrieved successfully", meetings));
    }

    // Get meeting by ID
    @GetMapping("/meetings/{id}")
    public ResponseEntity<ApiResponse<Meeting>> getMeetingById(@PathVariable Long id) {
        Meeting meeting = meetingService.getMeetingById(id);
        return ResponseEntity.ok(ApiResponse.success("Meeting retrieved successfully", meeting));
    }

    // Create new meeting
    @PostMapping("/meetings")
    public ResponseEntity<ApiResponse<Meeting>> createMeeting(@RequestBody MeetingRequest meetingRequest, @RequestParam String username) {
        Meeting savedMeeting = meetingService.createMeeting(meetingRequest, username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Meeting created successfully", savedMeeting));
    }

    // Update meeting
    @PutMapping("/meetings/{id}")
    public ResponseEntity<ApiResponse<Meeting>> updateMeeting(@PathVariable Long id, @RequestBody MeetingRequest meetingRequest, @RequestParam String username) {
        Meeting updatedMeeting = meetingService.updateMeeting(id, meetingRequest, username);
        return ResponseEntity.ok(ApiResponse.success("Meeting updated successfully", updatedMeeting));
    }

    // Update meeting status
    @PatchMapping("/meetings/{id}/status")
    public ResponseEntity<ApiResponse<Meeting>> updateMeetingStatus(@PathVariable Long id, @RequestParam MeetingStatus status, @RequestParam String username) {
        Meeting updatedMeeting = meetingService.updateMeetingStatus(id, status, username);
        return ResponseEntity.ok(ApiResponse.success("Meeting status updated successfully", updatedMeeting));
    }

    // Delete meeting
    @DeleteMapping("/meetings/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMeeting(@PathVariable Long id, @RequestParam String username) {
        meetingService.deleteMeeting(id, username);
        return ResponseEntity.ok(ApiResponse.success("Meeting deleted successfully", null));
    }

    // Get meetings by creator
    @GetMapping("/meetings/created-by/{username}")
    public ResponseEntity<ApiResponse<List<Meeting>>> getMeetingsByCreator(@PathVariable String username) {
        List<Meeting> meetings = meetingService.getMeetingsByCreator(username);
        return ResponseEntity.ok(ApiResponse.success("Meetings by creator retrieved successfully", meetings));
    }

    // Get meetings by status
    @GetMapping("/meetings/status/{status}")
    public ResponseEntity<ApiResponse<List<Meeting>>> getMeetingsByStatus(@PathVariable MeetingStatus status) {
        List<Meeting> meetings = meetingService.getMeetingsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Meetings by status retrieved successfully", meetings));
    }

    // Get meetings by employee ID
    @GetMapping("/meetings/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<Meeting>>> getMeetingsByEmployeeId(@PathVariable Long employeeId) {
        List<Meeting> meetings = meetingService.getMeetingsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Meetings for employee retrieved successfully", meetings));
    }

} 