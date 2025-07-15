package net.javaguides.springboot_backend.service;

import net.javaguides.springboot_backend.entity.Project;
import net.javaguides.springboot_backend.entity.Task;
import net.javaguides.springboot_backend.entity.Task.TaskPriority;
import net.javaguides.springboot_backend.entity.Task.TaskStatus;
import net.javaguides.springboot_backend.entity.Task.TaskStage;
import net.javaguides.springboot_backend.entity.TaskFile;
import net.javaguides.springboot_backend.exception.ResourceNotFoundException;
import net.javaguides.springboot_backend.repositories.ProjectRepository;
import net.javaguides.springboot_backend.repositories.TaskRepository;
import net.javaguides.springboot_backend.repositories.TaskFileRepository;
import net.javaguides.springboot_backend.service.AuditService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
@Transactional
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private TaskFileRepository taskFileRepository;
    
    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    // Get task by ID
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }
    
    // Get all tasks by project ID
    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }
    
    // Create a new task for a project
    public Task createTask(Task task, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        task.setProject(project);
        validateTask(task);
        setDefaultValues(task);
        return taskRepository.save(task);
    }
    
    // Update an existing task
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Update fields
        task.setName(taskDetails.getName());
        task.setDescription(taskDetails.getDescription());
        task.setAssignedTo(taskDetails.getAssignedTo());
        task.setPriority(taskDetails.getPriority());
        task.setStatus(taskDetails.getStatus());
        task.setProgress(taskDetails.getProgress());
        task.setDeadline(taskDetails.getDeadline());
        task.setStage(taskDetails.getStage());
        
        validateTask(task);
        return taskRepository.save(task);
    }
    
    // Partial update of a task
    public Task partialUpdateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Update only non-null fields
        if (taskDetails.getName() != null) {
            task.setName(taskDetails.getName());
        }
        if (taskDetails.getDescription() != null) {
            task.setDescription(taskDetails.getDescription());
        }
        if (taskDetails.getAssignedTo() != null) {
            task.setAssignedTo(taskDetails.getAssignedTo());
        }
        if (taskDetails.getPriority() != null) {
            task.setPriority(taskDetails.getPriority());
        }
        if (taskDetails.getStatus() != null) {
            task.setStatus(taskDetails.getStatus());
        }
        if (taskDetails.getProgress() != null) {
            task.setProgress(taskDetails.getProgress());
        }
        if (taskDetails.getDeadline() != null) {
            task.setDeadline(taskDetails.getDeadline());
        }
        if (taskDetails.getStage() != null) {
            task.setStage(taskDetails.getStage());
        }
        
        validateTask(task);
        return taskRepository.save(task);
    }
    
    // Delete task
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
    
    // Get tasks by status for a project
    public List<Task> getTasksByProjectIdAndStatus(Long projectId, TaskStatus status) {
        return taskRepository.findByProjectIdAndStatus(projectId, status);
    }
    
    // Get tasks by priority for a project
    public List<Task> getTasksByProjectIdAndPriority(Long projectId, TaskPriority priority) {
        return taskRepository.findByProjectIdAndPriority(projectId, priority);
    }
    
    // Get tasks by stage for a project
    public List<Task> getTasksByProjectIdAndStage(Long projectId, TaskStage stage) {
        return taskRepository.findByProjectIdAndStage(projectId, stage);
    }
    
    // Get tasks by assigned person for a project
    public List<Task> getTasksByProjectIdAndAssignedTo(Long projectId, String assignedTo) {
        return taskRepository.findByProjectIdAndAssignedTo(projectId, assignedTo);
    }
    
    // Get overdue tasks for a project
    public List<Task> getOverdueTasksByProjectId(Long projectId) {
        return taskRepository.findOverdueTasksByProjectId(projectId, LocalDate.now());
    }
    
    // Get tasks due soon for a project
    public List<Task> getTasksDueSoonByProjectId(Long projectId) {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        return taskRepository.findTasksDueSoonByProjectId(projectId, today, nextWeek);
    }
    
    // Get task statistics for a project
    public Map<String, Object> getTaskStatisticsByProjectId(Long projectId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // Total tasks
        Long totalTasks = taskRepository.countTasksByProjectId(projectId);
        statistics.put("totalTasks", totalTasks);
        
        // Tasks by status
        Long pendingTasks = taskRepository.countTasksByProjectIdAndStatus(projectId, TaskStatus.PENDING);
        Long inProgressTasks = taskRepository.countTasksByProjectIdAndStatus(projectId, TaskStatus.IN_PROGRESS);
        Long completedTasks = taskRepository.countTasksByProjectIdAndStatus(projectId, TaskStatus.COMPLETED);
        Long onHoldTasks = taskRepository.countTasksByProjectIdAndStatus(projectId, TaskStatus.ON_HOLD);
        
        statistics.put("pendingTasks", pendingTasks);
        statistics.put("inProgressTasks", inProgressTasks);
        statistics.put("completedTasks", completedTasks);
        statistics.put("onHoldTasks", onHoldTasks);
        
        // Tasks by priority
        Long highPriorityTasks = taskRepository.countTasksByProjectIdAndPriority(projectId, TaskPriority.HIGH);
        Long mediumPriorityTasks = taskRepository.countTasksByProjectIdAndPriority(projectId, TaskPriority.MEDIUM);
        Long lowPriorityTasks = taskRepository.countTasksByProjectIdAndPriority(projectId, TaskPriority.LOW);
        
        statistics.put("highPriorityTasks", highPriorityTasks);
        statistics.put("mediumPriorityTasks", mediumPriorityTasks);
        statistics.put("lowPriorityTasks", lowPriorityTasks);
        
        // Tasks by stage
        Long developmentTasks = taskRepository.countTasksByProjectIdAndStage(projectId, TaskStage.DEVELOPMENT);
        Long testingTasks = taskRepository.countTasksByProjectIdAndStage(projectId, TaskStage.TESTING);
        Long stagingTasks = taskRepository.countTasksByProjectIdAndStage(projectId, TaskStage.STAGING);
        Long productionTasks = taskRepository.countTasksByProjectIdAndStage(projectId, TaskStage.PRODUCTION);
        
        statistics.put("developmentTasks", developmentTasks);
        statistics.put("testingTasks", testingTasks);
        statistics.put("stagingTasks", stagingTasks);
        statistics.put("productionTasks", productionTasks);
        
        // Average progress
        Double averageProgress = taskRepository.getAverageProgressByProjectId(projectId);
        statistics.put("averageProgress", averageProgress != null ? averageProgress : 0.0);
        
        // Overdue tasks
        List<Task> overdueTasks = getOverdueTasksByProjectId(projectId);
        statistics.put("overdueTasks", overdueTasks.size());
        
        // Tasks due soon
        List<Task> tasksDueSoon = getTasksDueSoonByProjectId(projectId);
        statistics.put("tasksDueSoon", tasksDueSoon.size());
        
        return statistics;
    }
    
    // Update task status with automatic progress synchronization
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Task existingTask = getTaskById(taskId);
        existingTask.setStatus(status);
        
        // Auto-sync progress based on status
        switch (status) {
            case COMPLETED:
                existingTask.setProgress(100);
                break;
            case IN_PROGRESS:
                // Only set to IN_PROGRESS if current progress is 0
                if (existingTask.getProgress() == 0) {
                    existingTask.setProgress(25); // Default progress when starting
                }
                break;
            case PENDING:
                // Reset progress to 0 only if it was completed
                if (existingTask.getProgress() == 100) {
                    existingTask.setProgress(0);
                }
                break;
            case ON_HOLD:
                // Keep current progress when putting on hold
                break;
        }
        
        existingTask.setUpdatedAt(LocalDate.now());
        return taskRepository.save(existingTask);
    }
    
    // Update task progress with automatic status synchronization
    public Task updateTaskProgress(Long taskId, Integer progress) {
        Task existingTask = getTaskById(taskId);
        existingTask.setProgress(progress);
        
        // Auto-sync status based on progress, but preserve ON_HOLD status
        if (existingTask.getStatus() != TaskStatus.ON_HOLD) {
            if (progress >= 100) {
                existingTask.setStatus(TaskStatus.COMPLETED);
            } else if (progress > 0) {
                existingTask.setStatus(TaskStatus.IN_PROGRESS);
            } else if (progress == 0) {
                existingTask.setStatus(TaskStatus.PENDING);
            }
        }
        // ON_HOLD status is preserved regardless of progress
        
        existingTask.setUpdatedAt(LocalDate.now());
        return taskRepository.save(existingTask);
    }
    
    // Unified method to update both status and progress with auto-sync
    public Task updateTaskStatusAndProgress(Long taskId, TaskStatus status, Integer progress) {
        Task existingTask = getTaskById(taskId);
        
        // Update status if provided
        if (status != null) {
            existingTask.setStatus(status);
            
            // Auto-sync progress based on status
            switch (status) {
                case COMPLETED:
                    existingTask.setProgress(100);
                    break;
                case IN_PROGRESS:
                    // Only set to IN_PROGRESS if current progress is 0
                    if (existingTask.getProgress() == 0) {
                        existingTask.setProgress(25); // Default progress when starting
                    }
                    break;
                case PENDING:
                    // Reset progress to 0 only if it was completed
                    if (existingTask.getProgress() == 100) {
                        existingTask.setProgress(0);
                    }
                    break;
                case ON_HOLD:
                    // Keep current progress when putting on hold
                    break;
            }
        }
        
        // Update progress if provided
        if (progress != null) {
            existingTask.setProgress(progress);
            
            // Auto-sync status based on progress, but preserve ON_HOLD status
            if (existingTask.getStatus() != TaskStatus.ON_HOLD) {
                if (progress >= 100) {
                    existingTask.setStatus(TaskStatus.COMPLETED);
                } else if (progress > 0) {
                    existingTask.setStatus(TaskStatus.IN_PROGRESS);
                } else if (progress == 0) {
                    existingTask.setStatus(TaskStatus.PENDING);
                }
            }
            // ON_HOLD status is preserved regardless of progress
        }
        
        existingTask.setUpdatedAt(LocalDate.now());
        return taskRepository.save(existingTask);
    }
    
    // Assign task to a person
    public Task assignTask(Long taskId, String assignedTo) {
        Task existingTask = getTaskById(taskId);
        existingTask.setAssignedTo(assignedTo);
        existingTask.setUpdatedAt(LocalDate.now());
        return taskRepository.save(existingTask);
    }
    
    public String uploadTaskFile(Long taskId, MultipartFile file, String username) throws Exception {
        // Fetch the Task entity
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // Create and save TaskFile entity with file data as BLOB
        TaskFile taskFile = TaskFile.builder()
            .fileName(file.getOriginalFilename())
            .fileType(file.getContentType())
            .fileSize(file.getSize())
            .data(file.getBytes())
            .uploadedBy(username)
            .task(task)
            .build();
        taskFileRepository.save(taskFile);

        return file.getOriginalFilename();
    }

    public TaskFile getTaskFileById(Long fileId) {
        return taskFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Task file not found"));
    }

    public void deleteTaskFile(Long fileId, String username, String taskName, String fileName) {
        if (!taskFileRepository.existsById(fileId)) {
            throw new ResourceNotFoundException("Task file not found");
        }
        taskFileRepository.deleteById(fileId);

        // Audit trail
        auditService.createAuditTrail("Deleted " + fileName + " for task " + taskName, username);
    }


    
    private void validateTask(Task task) {
        if (task.getName() == null || task.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name is required");
        }
        if (task.getAssignedTo() == null || task.getAssignedTo().trim().isEmpty()) {
            throw new IllegalArgumentException("Task assignee is required");
        }
        if (task.getProgress() != null && (task.getProgress() < 0 || task.getProgress() > 100)) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
    }
    
    private void setDefaultValues(Task task) {
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        if (task.getProgress() == null) {
            task.setProgress(0);
        }
        if (task.getStage() == null) {
            task.setStage(TaskStage.DEVELOPMENT);
        }
    }
} 