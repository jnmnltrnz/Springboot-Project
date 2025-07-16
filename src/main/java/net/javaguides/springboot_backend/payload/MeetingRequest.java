package net.javaguides.springboot_backend.payload;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class MeetingRequest {
    private String title;
    private LocalDate date;
    private LocalTime time;
    private String notes;
    private List<Long> inviteeIds;

    // Default constructor
    public MeetingRequest() {}

    // Constructor with all fields
    public MeetingRequest(String title, LocalDate date, LocalTime time, String notes, List<Long> inviteeIds) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.notes = notes;
        this.inviteeIds = inviteeIds;
    }

    // Getters and setters
    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }

    public LocalDate getDate() { 
        return date; 
    }
    
    public void setDate(LocalDate date) { 
        this.date = date; 
    }

    public LocalTime getTime() { 
        return time; 
    }
    
    public void setTime(LocalTime time) { 
        this.time = time; 
    }

    public String getNotes() { 
        return notes; 
    }
    
    public void setNotes(String notes) { 
        this.notes = notes; 
    }

    public List<Long> getInviteeIds() {
        return inviteeIds;
    }

    public void setInviteeIds(List<Long> inviteeIds) {
        this.inviteeIds = inviteeIds;
    }
} 