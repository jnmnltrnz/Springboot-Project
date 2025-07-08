package net.javaguides.springboot_backend.controller;

import jakarta.transaction.Transactional;
import net.javaguides.springboot_backend.entity.Employee;
import net.javaguides.springboot_backend.entity.Project;
import net.javaguides.springboot_backend.repositories.EmployeeRepository;
import net.javaguides.springboot_backend.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Get all projects
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        try {
            List<Project> projects = projectRepository.findAll();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get project by ID
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        try {
            Optional<Project> project = projectRepository.findById(id);
            return project.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new project
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        try {
            if (project.getName() == null || project.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (project.getManager() == null || project.getManager().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Set default values if not provided
            if (project.getStatus() == null) {
                project.setStatus(Project.ProjectStatus.PLANNING);
            }
            if (project.getProgress() == null) {
                project.setProgress(0);
            }
            if (project.getTeamSize() == null) {
                project.setTeamSize(1);
            }

            Project savedProject = projectRepository.save(project);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update project
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        try {
            Optional<Project> projectOptional = projectRepository.findById(id);
            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();

                project.setName(projectDetails.getName());
                project.setManager(projectDetails.getManager());
                project.setStatus(projectDetails.getStatus());
                project.setProgress(projectDetails.getProgress());
                project.setDeadline(projectDetails.getDeadline());
                project.setTeamSize(projectDetails.getTeamSize());
                project.setDescription(projectDetails.getDescription());

                Project updatedProject = projectRepository.save(project);
                return ResponseEntity.ok(updatedProject);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete project
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        try {
            if (projectRepository.existsById(id)) {
                projectRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{projectId}/assign-employees")
    @Transactional
    public ResponseEntity<Project> assignEmployeesToProject(
            @PathVariable Long projectId,
            @RequestBody List<Long> employeeIds) {
        try {
            Optional<Project> projectOptional = projectRepository.findById(projectId);
            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();

                // Get all employees to be assigned
                List<Employee> employeesToAssign = employeeRepository.findAllById(employeeIds);

                // Clear existing assignments and add new ones
                project.getAssignedEmployees().clear();
                project.getAssignedEmployees().addAll(employeesToAssign);

                // Update team size
                project.setTeamSize(employeesToAssign.size());

                // Save the project
                Project savedProject = projectRepository.save(project);

                // Force flush to ensure changes are persisted
                projectRepository.flush();

                return ResponseEntity.ok(savedProject);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Remove employees from project
    @DeleteMapping("/{projectId}/remove-employees")
    public ResponseEntity<Project> removeEmployeesFromProject(
            @PathVariable Long projectId,
            @RequestBody List<Long> employeeIds) {
        try {
            Optional<Project> projectOptional = projectRepository.findById(projectId);
            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();

                for (Long employeeId : employeeIds) {
                    Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
                    if (employeeOptional.isPresent()) {
                        project.removeEmployee(employeeOptional.get());
                    }
                }

                Project updatedProject = projectRepository.save(project);
                return ResponseEntity.ok(updatedProject);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get projects by employee ID
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Project>> getProjectsByEmployee(@PathVariable Long employeeId) {
        try {
            List<Project> projects = projectRepository.findProjectsByEmployeeId(employeeId);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}