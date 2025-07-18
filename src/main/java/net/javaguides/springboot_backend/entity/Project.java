package net.javaguides.springboot_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "manager", nullable = false)
    private String manager;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Column(name = "progress")
    private Integer progress = 0;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "team_size")
    private Integer teamSize = 1;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "project_employees",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> assignedEmployees = new HashSet<>();

    // Getter for assigned employees
    public Set<Employee> getAssignedEmployees() {
        return assignedEmployees;
    }

    public void setAssignedEmployees(Set<Employee> assignedEmployees) {
        this.assignedEmployees = assignedEmployees;
    }

    // Method to get employee IDs for JSON serialization
    public Set<Long> getAssignedEmployeeIds() {
        Set<Long> employeeIds = new HashSet<>();
        if (assignedEmployees != null) {
            for (Employee employee : assignedEmployees) {
                employeeIds.add(employee.getId());
            }
        }
        return employeeIds;
    }

    // Method to get employee names for display
    public Set<String> getAssignedEmployeeNames() {
        Set<String> employeeNames = new HashSet<>();
        if (assignedEmployees != null) {
            for (Employee employee : assignedEmployees) {
                employeeNames.add(employee.getFirstName() + " " + employee.getLastName());
            }
        }
        return employeeNames;
    }

    // Constructors
    public Project() {
        this.createdAt = LocalDate.now();
        this.status = ProjectStatus.PLANNING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Integer getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public void addEmployee(Employee employee) {
        this.assignedEmployees.add(employee);
        // Don't automatically update teamSize - it represents the maximum allowed team members
    }

    public void removeEmployee(Employee employee) {
        this.assignedEmployees.remove(employee);
        // Don't automatically update teamSize - it represents the maximum allowed team members
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDate.now();
    }

    // Project Status Enum
    public enum ProjectStatus {
        PLANNING("Planning"),
        IN_PROGRESS("In Progress"),
        ON_HOLD("On Hold"),
        COMPLETED("Completed");

        private final String displayName;

        ProjectStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}