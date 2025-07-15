package net.javaguides.springboot_backend.controller;

import net.javaguides.springboot_backend.entity.Task;
import net.javaguides.springboot_backend.entity.Task.TaskPriority;
import net.javaguides.springboot_backend.entity.Task.TaskStatus;
import net.javaguides.springboot_backend.entity.Task.TaskStage;
import net.javaguides.springboot_backend.payload.ApiResponse;

import net.javaguides.springboot_backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;
import net.javaguides.springboot_backend.entity.TaskFile;
import net.javaguides.springboot_backend.repositories.TaskFileRepository;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskFileRepository taskFileRepository;
    
    // Get all tasks
    @GetMapping
    public ResponseEntity<ApiResponse<List<Task>>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", tasks));
    }
    
    // Get task by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success("Task retrieved successfully", task));
    }
    
    // Get all tasks by project ID
    @GetMapping("/project/{projectId}/all")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByProjectId(@PathVariable Long projectId) {
        List<Task> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", tasks));
    }
    
    // Create a new task for a project
    @PostMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<Task>> createTask(@PathVariable Long projectId, @RequestBody Task task) {
        Task createdTask = taskService.createTask(task, projectId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", createdTask));
    }
    
    // Update task
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        Task updatedTask = taskService.updateTask(id, taskDetails);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", updatedTask));
    }
    
    // Delete task
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
    
    // Get tasks by status for a project
    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByProjectIdAndStatus(
            @PathVariable Long projectId, 
            @PathVariable TaskStatus status) {
        List<Task> tasks = taskService.getTasksByProjectIdAndStatus(projectId, status);
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", tasks));
    }
    
    // Get tasks by priority for a project
    @GetMapping("/project/{projectId}/priority/{priority}")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByProjectIdAndPriority(
            @PathVariable Long projectId, 
            @PathVariable TaskPriority priority) {
        List<Task> tasks = taskService.getTasksByProjectIdAndPriority(projectId, priority);
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", tasks));
    }
    
    // Get tasks by stage for a project
    @GetMapping("/project/{projectId}/stage/{stage}")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByProjectIdAndStage(
            @PathVariable Long projectId, 
            @PathVariable TaskStage stage) {
        List<Task> tasks = taskService.getTasksByProjectIdAndStage(projectId, stage);
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", tasks));
    }
    
    // Get tasks by assigned person for a project
    @GetMapping("/project/{projectId}/assigned/{assignedTo}")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByProjectIdAndAssignedTo(
            @PathVariable Long projectId, 
            @PathVariable String assignedTo) {
        List<Task> tasks = taskService.getTasksByProjectIdAndAssignedTo(projectId, assignedTo);
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", tasks));
    }
    
    // Get overdue tasks for a project
    @GetMapping("/project/{projectId}/overdue")
    public ResponseEntity<ApiResponse<List<Task>>> getOverdueTasksByProjectId(@PathVariable Long projectId) {
        List<Task> tasks = taskService.getOverdueTasksByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.success("Overdue tasks retrieved successfully", tasks));
    }
    
    // Get tasks due soon for a project
    @GetMapping("/project/{projectId}/due-soon")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksDueSoonByProjectId(@PathVariable Long projectId) {
        List<Task> tasks = taskService.getTasksDueSoonByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.success("Tasks due soon retrieved successfully", tasks));
    }
    
    // Get task statistics for a project
    @GetMapping("/project/{projectId}/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTaskStatisticsByProjectId(@PathVariable Long projectId) {
        Map<String, Object> statistics = taskService.getTaskStatisticsByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.success("Task statistics retrieved successfully", statistics));
    }
    
    // Update task status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Task>> updateTaskStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, TaskStatus> statusUpdate,
            @RequestParam String username) {
        TaskStatus newStatus = statusUpdate.get("status");
        Task updatedTask = taskService.updateTaskStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success("Task status updated successfully", updatedTask));
    }
    
    // Update task progress
    @PatchMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<Task>> updateTaskProgress(
            @PathVariable Long id, 
            @RequestBody Map<String, Integer> progressUpdate,
            @RequestParam String username) {
        Integer newProgress = progressUpdate.get("progress");
        Task updatedTask = taskService.updateTaskProgress(id, newProgress);
        return ResponseEntity.ok(ApiResponse.success("Task progress updated successfully", updatedTask));
    }
    
    // Unified update for both status and progress with auto-sync
    @PatchMapping("/{id}/update")
    public ResponseEntity<ApiResponse<Task>> updateTaskStatusAndProgress(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> updateData,
            @RequestParam String username) {
        TaskStatus newStatus = null;
        Integer newProgress = null;
        
        if (updateData.containsKey("status")) {
            newStatus = TaskStatus.valueOf(updateData.get("status").toString());
        }
        if (updateData.containsKey("progress")) {
            newProgress = Integer.valueOf(updateData.get("progress").toString());
        }
        
        Task updatedTask = taskService.updateTaskStatusAndProgress(id, newStatus, newProgress);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", updatedTask));
    }
    
    // Assign task to a person
    @PatchMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<Task>> assignTask(
            @PathVariable Long id, 
            @RequestBody Map<String, String> assignmentUpdate) {
        String assignedTo = assignmentUpdate.get("assignedTo");
        Task updatedTask = taskService.assignTask(id, assignedTo);
        return ResponseEntity.ok(ApiResponse.success("Task assigned successfully", updatedTask));
    }
    
    // Get all task priorities
    @GetMapping("/priorities")
    public ResponseEntity<ApiResponse<TaskPriority[]>> getTaskPriorities() {
        return ResponseEntity.ok(ApiResponse.success("Task priorities retrieved successfully", TaskPriority.values()));
    }
    
    // Get all task statuses
    @GetMapping("/statuses")
    public ResponseEntity<ApiResponse<TaskStatus[]>> getTaskStatuses() {
        return ResponseEntity.ok(ApiResponse.success("Task statuses retrieved successfully", TaskStatus.values()));
    }
    
    // Get all task stages
    @GetMapping("/stages")
    public ResponseEntity<ApiResponse<TaskStage[]>> getTaskStages() {
        return ResponseEntity.ok(ApiResponse.success("Task stages retrieved successfully", TaskStage.values()));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<ApiResponse<String>> uploadTaskFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam String username) {
        try {
            String result = taskService.uploadTaskFile(id, file, username);
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("File upload failed: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<ApiResponse<List<TaskFile>>> getTaskFiles(@PathVariable Long id) {
        List<TaskFile> files = taskFileRepository.findByTask_Id(id);
        return ResponseEntity.ok(ApiResponse.success("Files retrieved successfully", files));
    }

    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<byte[]> downloadTaskFile(@PathVariable Long fileId) {
        TaskFile file = taskService.getTaskFileById(fileId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"")
                .header("Content-Type", file.getFileType())
                .body(file.getData());
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<ApiResponse<String>> deleteTaskFile(@PathVariable Long fileId, @RequestParam String username, @RequestParam String taskName, @RequestParam String fileName) {
        taskService.deleteTaskFile(fileId, username, taskName, fileName);
        return ResponseEntity.ok(ApiResponse.success("Task file deleted successfully", null));
    }
}  