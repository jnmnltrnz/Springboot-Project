package net.javaguides.springboot_backend.payload;

import java.util.List;

public class MeetingRequest {
    private String title;
    private String date;
    private String time;
    private String notes;
    private List<Long> inviteeIds;

    // Default constructor
    public MeetingRequest() {}

    // Constructor with all fields
    public MeetingRequest(String title, String date, String time, String notes, List<Long> inviteeIds) {
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

    public String getDate() { 
        return date; 
    }
    
    public void setDate(String date) { 
        this.date = date; 
    }

    public String getTime() { 
        return time; 
    }
    
    public void setTime(String time) { 
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