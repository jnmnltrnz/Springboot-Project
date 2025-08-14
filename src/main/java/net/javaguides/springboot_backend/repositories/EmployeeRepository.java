package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByFirstNameNot(String firstName);

    List<Employee> findByDepartment(String department);

    List<Employee> findByPosition(String position);

    @Query("SELECT e FROM Employee e WHERE e.hireDate >= :startDate")
    List<Employee> findEmployeesHiredAfter(@Param("startDate") LocalDate startDate);

    @Query("SELECT e FROM Employee e WHERE e.salary >= :minSalary")
    List<Employee> findEmployeesWithSalaryAbove(@Param("minSalary") Double minSalary);

    @Query("SELECT DISTINCT e.department FROM Employee e")
    List<String> findAllDepartments();

    @Query("SELECT DISTINCT e.position FROM Employee e")
    List<String> findAllPositions();

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department = :department")
    long countByDepartment(@Param("department") String department);

    Optional<Employee> findByAccount_Id(Long accountId);

}
