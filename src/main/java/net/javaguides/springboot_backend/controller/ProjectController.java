package net.javaguides.springboot_backend.controller;

import net.javaguides.springboot_backend.entity.Employee;
import net.javaguides.springboot_backend.entity.Project;
import net.javaguides.springboot_backend.payload.ApiResponse;
import net.javaguides.springboot_backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Get all projects
    @GetMapping
    public ResponseEntity<ApiResponse<List<Project>>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", projects));
    }

    // Get project by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Project>> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success("Project retrieved successfully", project));
    }

    // Create new project
    @PostMapping
    public ResponseEntity<ApiResponse<Project>> createProject(@RequestBody Project project) {
        Project savedProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", savedProject));
    }

    // Update project
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Project>> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        Project updatedProject = projectService.updateProject(id, projectDetails);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", updatedProject));
    }

    // Delete project
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
    }

    @PostMapping("/{projectId}/assign-employees")
    public ResponseEntity<ApiResponse<Project>> assignEmployeesToProject(
            @PathVariable Long projectId,
            @RequestBody List<Long> employeeIds) {
        Project savedProject = projectService.assignEmployeesToProject(projectId, employeeIds);
        return ResponseEntity.ok(ApiResponse.success("Employees assigned to project successfully", savedProject));
    }

    // Remove employees from project
    @DeleteMapping("/{projectId}/remove-employees")
    public ResponseEntity<ApiResponse<Project>> removeEmployeesFromProject(
            @PathVariable Long projectId,
            @RequestBody List<Long> employeeIds) {
        Project updatedProject = projectService.removeEmployeesFromProject(projectId, employeeIds);
        return ResponseEntity.ok(ApiResponse.success("Employees removed from project successfully", updatedProject));
    }

    // Get projects by employee ID
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsByEmployee(@PathVariable Long employeeId) {
        List<Project> projects = projectService.getProjectsByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", projects));
    }

}