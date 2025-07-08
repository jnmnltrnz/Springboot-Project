package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByFirstNameNot(String firstName);
}
