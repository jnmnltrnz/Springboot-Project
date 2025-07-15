package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.Meeting;
import net.javaguides.springboot_backend.status.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("SELECT DISTINCT m FROM Meeting m LEFT JOIN FETCH m.invitees ORDER BY m.meetingDate ASC, m.meetingTime ASC")
    List<Meeting> findAllWithInvitees();

    List<Meeting> findByMeetingDateOrderByMeetingTimeAsc(LocalDate meetingDate);
    
    List<Meeting> findByMeetingDateGreaterThanEqualOrderByMeetingDateAscMeetingTimeAsc(LocalDate meetingDate);
    
    List<Meeting> findByStatus(MeetingStatus status);
    
    @Query("SELECT m FROM Meeting m WHERE m.meetingDate >= :startDate ORDER BY m.meetingDate ASC, m.meetingTime ASC")
    List<Meeting> findUpcomingMeetings(@Param("startDate") LocalDate startDate);
    
    List<Meeting> findByCreatedByOrderByCreatedAtDesc(String createdBy);
    
    // Find meetings by date range
    @Query("SELECT m FROM Meeting m WHERE m.meetingDate BETWEEN :startDate AND :endDate ORDER BY m.meetingDate ASC, m.meetingTime ASC")
    List<Meeting> findMeetingsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find meetings by title containing keyword
    List<Meeting> findByTitleContainingIgnoreCase(String title);
    
    // Find meetings created after a specific date
    List<Meeting> findByCreatedAtAfter(LocalDateTime createdAt);
    
    // Find meetings by status and creator
    List<Meeting> findByStatusAndCreatedBy(MeetingStatus status, String createdBy);
    
    // Count meetings by status
    long countByStatus(MeetingStatus status);
    
    // Count meetings by creator
    long countByCreatedBy(String createdBy);
    
    // Find today's meetings
    @Query("SELECT m FROM Meeting m WHERE m.meetingDate = CURRENT_DATE ORDER BY m.meetingTime ASC")
    List<Meeting> findTodaysMeetings();
    
    // Find meetings by employee ID (invitee)
    @Query("SELECT DISTINCT m FROM Meeting m LEFT JOIN FETCH m.invitees WHERE :employeeId IN (SELECT e.id FROM m.invitees e) ORDER BY m.meetingDate ASC, m.meetingTime ASC")
    List<Meeting> findMeetingsByEmployeeId(@Param("employeeId") Long employeeId);
} 