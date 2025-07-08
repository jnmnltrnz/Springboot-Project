package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
	List<Document> findByEmployeeId(Long employeeId);
}