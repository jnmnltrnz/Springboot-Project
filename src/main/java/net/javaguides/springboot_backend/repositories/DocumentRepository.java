package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
	@Query("SELECT d FROM Document d WHERE d.employee.id = :employeeId")
	List<Document> findByEmployeeId(@Param("employeeId") Long employeeId);
}