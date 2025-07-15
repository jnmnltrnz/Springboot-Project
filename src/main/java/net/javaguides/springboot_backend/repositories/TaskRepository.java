package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.Task;
import net.javaguides.springboot_backend.entity.Task.TaskPriority;
import net.javaguides.springboot_backend.entity.Task.TaskStatus;
import net.javaguides.springboot_backend.entity.Task.TaskStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Find all tasks by project ID
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId")
    List<Task> findByProjectId(@Param("projectId") Long projectId);
    
    // Find tasks by project ID and status
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    List<Task> findByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);
    
    // Find tasks by project ID and priority
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.priority = :priority")
    List<Task> findByProjectIdAndPriority(@Param("projectId") Long projectId, @Param("priority") TaskPriority priority);
    
    // Find tasks by project ID and stage
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.stage = :stage")
    List<Task> findByProjectIdAndStage(@Param("projectId") Long projectId, @Param("stage") TaskStage stage);
    
    // Find tasks by assigned person
    List<Task> findByAssignedTo(String assignedTo);
    
    // Find tasks by project ID and assigned person
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.assignedTo = :assignedTo")
    List<Task> findByProjectIdAndAssignedTo(@Param("projectId") Long projectId, @Param("assignedTo") String assignedTo);
    
    // Find tasks with deadline before a specific date
    List<Task> findByDeadlineBefore(LocalDate date);
    
    // Find tasks by project ID with deadline before a specific date
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.deadline < :date")
    List<Task> findByProjectIdAndDeadlineBefore(@Param("projectId") Long projectId, @Param("date") LocalDate date);
    
    // Find tasks by project ID and progress less than 100
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.progress < :progress")
    List<Task> findByProjectIdAndProgressLessThan(@Param("projectId") Long projectId, @Param("progress") Integer progress);
    
    // Find tasks by project ID and progress greater than 0
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.progress > :progress")
    List<Task> findByProjectIdAndProgressGreaterThan(@Param("projectId") Long projectId, @Param("progress") Integer progress);
    
    // Custom query to get task statistics for a project
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId")
    Long countTasksByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    Long countTasksByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.priority = :priority")
    Long countTasksByProjectIdAndPriority(@Param("projectId") Long projectId, @Param("priority") TaskPriority priority);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.stage = :stage")
    Long countTasksByProjectIdAndStage(@Param("projectId") Long projectId, @Param("stage") TaskStage stage);
    
    // Get average progress for a project
    @Query("SELECT AVG(t.progress) FROM Task t WHERE t.project.id = :projectId")
    Double getAverageProgressByProjectId(@Param("projectId") Long projectId);
    
    // Find overdue tasks (deadline before today)
    @Query("SELECT t FROM Task t WHERE t.deadline < :today AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);
    
    // Find overdue tasks for a specific project
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.deadline < :today AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasksByProjectId(@Param("projectId") Long projectId, @Param("today") LocalDate today);
    
    // Find tasks due soon (within next 7 days)
    @Query("SELECT t FROM Task t WHERE t.deadline BETWEEN :today AND :nextWeek AND t.status != 'COMPLETED'")
    List<Task> findTasksDueSoon(@Param("today") LocalDate today, @Param("nextWeek") LocalDate nextWeek);
    
    // Find tasks due soon for a specific project
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.deadline BETWEEN :today AND :nextWeek AND t.status != 'COMPLETED'")
    List<Task> findTasksDueSoonByProjectId(@Param("projectId") Long projectId, @Param("today") LocalDate today, @Param("nextWeek") LocalDate nextWeek);
} 