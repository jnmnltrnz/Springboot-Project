package net.javaguides.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import net.javaguides.springboot_backend.status.MeetingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;

    @Column(name = "meeting_time", nullable = false)
    private LocalTime meetingTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "meeting_invitees",
        joinColumns = @JoinColumn(name = "meeting_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> invitees;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MeetingStatus status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
} 