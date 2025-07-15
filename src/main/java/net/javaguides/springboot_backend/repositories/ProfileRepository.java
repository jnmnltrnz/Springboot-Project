package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.ProfileEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEmployee, Long> {
    @Query("SELECT p FROM ProfileEmployee p WHERE p.employee.id = :employeeId")
    ProfileEmployee findByEmployeeId(@Param("employeeId") Long employeeId);
}