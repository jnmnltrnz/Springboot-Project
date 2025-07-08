package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.Meeting;
import net.javaguides.springboot_backend.status.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByMeetingDateOrderByMeetingTimeAsc(LocalDate meetingDate);
    
    List<Meeting> findByMeetingDateGreaterThanEqualOrderByMeetingDateAscMeetingTimeAsc(LocalDate meetingDate);
    
    List<Meeting> findByStatus(MeetingStatus status);
    
    @Query("SELECT m FROM Meeting m WHERE m.meetingDate >= :startDate ORDER BY m.meetingDate ASC, m.meetingTime ASC")
    List<Meeting> findUpcomingMeetings(@Param("startDate") LocalDate startDate);
    
    List<Meeting> findByCreatedByOrderByCreatedAtDesc(String createdBy);
} 