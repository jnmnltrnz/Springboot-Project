package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.Project;
import net.javaguides.springboot_backend.status.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find projects by status
    List<Project> findByStatus(ProjectStatus status);

    // Find projects by manager
    List<Project> findByManagerContainingIgnoreCase(String manager);

    // Find projects by name containing (case-insensitive)
    List<Project> findByNameContainingIgnoreCase(String name);

    // Find projects with deadline before a specific date
    List<Project> findByDeadlineBefore(LocalDate date);

    // Find projects with deadline after a specific date
    List<Project> findByDeadlineAfter(LocalDate date);

    // Find projects with progress greater than or equal to a value
    List<Project> findByProgressGreaterThanEqual(Integer progress);

    // Find projects with progress less than or equal to a value
    List<Project> findByProgressLessThanEqual(Integer progress);

    // Find projects by team size
    List<Project> findByTeamSize(Integer teamSize);

    // Find projects by team size greater than or equal to a value
    List<Project> findByTeamSizeGreaterThanEqual(Integer teamSize);

    // Find projects created after a specific date
    List<Project> findByCreatedAtAfter(LocalDate date);

    // Find projects created before a specific date
    List<Project> findByCreatedAtBefore(LocalDate date);

    // Find projects by status and manager
    List<Project> findByStatusAndManagerContainingIgnoreCase(ProjectStatus status, String manager);

    // Find projects by status and progress range
    List<Project> findByStatusAndProgressBetween(ProjectStatus status, Integer minProgress, Integer maxProgress);

    // Custom query to find projects with specific employee assigned
    @Query("SELECT p FROM Project p JOIN p.assignedEmployees e WHERE e.id = :employeeId")
    List<Project> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);

    // Custom query to find projects with no employees assigned
    @Query("SELECT p FROM Project p WHERE p.assignedEmployees IS EMPTY")
    List<Project> findProjectsWithNoEmployees();

    // Custom query to find projects with team size greater than average
    @Query("SELECT p FROM Project p WHERE p.teamSize > (SELECT AVG(p2.teamSize) FROM Project p2)")
    List<Project> findProjectsWithTeamSizeAboveAverage();

    // Custom query to find projects with high progress (>= 80%)
    @Query("SELECT p FROM Project p WHERE p.progress >= 80")
    List<Project> findHighProgressProjects();

    // Custom query to find projects with low progress (<= 20%)
    @Query("SELECT p FROM Project p WHERE p.progress <= 20")
    List<Project> findLowProgressProjects();

    // Custom query to find overdue projects
    @Query("SELECT p FROM Project p WHERE p.deadline < :currentDate AND p.status != 'COMPLETED'")
    List<Project> findOverdueProjects(@Param("currentDate") LocalDate currentDate);

    // Custom query to find projects due soon (within next 7 days)
    @Query("SELECT p FROM Project p WHERE p.deadline BETWEEN :currentDate AND :weekFromNow AND p.status != 'COMPLETED'")
    List<Project> findProjectsDueSoon(@Param("currentDate") LocalDate currentDate, @Param("weekFromNow") LocalDate weekFromNow);

    // Count projects by status
    long countByStatus(ProjectStatus status);

    // Count projects with progress greater than or equal to a value
    long countByProgressGreaterThanEqual(Integer progress);

    // Count projects with team size greater than or equal to a value
    long countByTeamSizeGreaterThanEqual(Integer teamSize);

    // Check if project exists by name
    boolean existsByName(String name);

    // Find project by name (exact match)
    Optional<Project> findByName(String name);

    // Find projects ordered by creation date (newest first)
    List<Project> findAllByOrderByCreatedAtDesc();

    // Find projects ordered by deadline (earliest first)
    List<Project> findAllByOrderByDeadlineAsc();

    // Find projects ordered by progress (highest first)
    List<Project> findAllByOrderByProgressDesc();

    // Find projects ordered by team size (largest first)
    List<Project> findAllByOrderByTeamSizeDesc();
}