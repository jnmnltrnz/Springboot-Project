package net.javaguides.springboot_backend.status;

public enum MeetingStatus {
    SCHEDULED("Scheduled"),
    CANCELLED("Cancelled");

    private final String displayName;

    MeetingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 