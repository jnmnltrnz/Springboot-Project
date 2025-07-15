package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.TaskFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskFileRepository extends JpaRepository<TaskFile, Long> {
    List<TaskFile> findByTask_Id(Long taskId);
}
