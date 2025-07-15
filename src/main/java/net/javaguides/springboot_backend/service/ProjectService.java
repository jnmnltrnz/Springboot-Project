package net.javaguides.springboot_backend.service;

import net.javaguides.springboot_backend.entity.Employee;
import net.javaguides.springboot_backend.entity.Project;
import net.javaguides.springboot_backend.exception.ResourceNotFoundException;
import net.javaguides.springboot_backend.repositories.EmployeeRepository;
import net.javaguides.springboot_backend.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAllWithAssignedEmployees();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    public Project createProject(Project project) {
        validateProject(project);
        setDefaultValues(project);
        return projectRepository.save(project);
    }

    public Project updateProject(Long id, Project projectDetails) {
        Project project = getProjectById(id);
        
        project.setName(projectDetails.getName());
        project.setManager(projectDetails.getManager());
        project.setStatus(projectDetails.getStatus());
        project.setProgress(projectDetails.getProgress());
        project.setDeadline(projectDetails.getDeadline());
        project.setTeamSize(projectDetails.getTeamSize());
        project.setDescription(projectDetails.getDescription());

        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    public Project assignEmployeesToProject(Long projectId, List<Long> employeeIds) {
        Project project = getProjectById(projectId);
        List<Employee> employeesToAssign = employeeRepository.findAllById(employeeIds);

        // Clear existing assignments and add new ones
        project.getAssignedEmployees().clear();
        project.getAssignedEmployees().addAll(employeesToAssign);

        // Don't automatically update team size - it represents the maximum allowed team members
        // project.setTeamSize(employeesToAssign.size());

        return projectRepository.save(project);
    }

    public Project removeEmployeesFromProject(Long projectId, List<Long> employeeIds) {
        Project project = getProjectById(projectId);

        for (Long employeeId : employeeIds) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
            project.removeEmployee(employee);
        }

        return projectRepository.save(project);
    }

    public List<Project> getProjectsByEmployee(Long employeeId) {
        return projectRepository.findProjectsByEmployeeIdWithAssignedEmployees(employeeId);
    }

    private void validateProject(Project project) {
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (project.getManager() == null || project.getManager().trim().isEmpty()) {
            throw new IllegalArgumentException("Project manager is required");
        }
    }

    private void setDefaultValues(Project project) {
        if (project.getStatus() == null) {
            project.setStatus(Project.ProjectStatus.PLANNING);
        }
        if (project.getProgress() == null) {
            project.setProgress(0);
        }
        if (project.getTeamSize() == null) {
            project.setTeamSize(1);
        }
    }
} 