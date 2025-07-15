package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.TaskPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskPostRepository extends JpaRepository<TaskPost, Long> {
    
    // Find all posts by task ID, ordered by creation date (newest first)
    @Query("SELECT tp FROM TaskPost tp WHERE tp.task.id = :taskId ORDER BY tp.createdAt DESC")
    List<TaskPost> findByTaskIdOrderByCreatedAtDesc(@Param("taskId") Long taskId);
    
    // Find all posts by task ID, ordered by creation date (oldest first)
    @Query("SELECT tp FROM TaskPost tp WHERE tp.task.id = :taskId ORDER BY tp.createdAt ASC")
    List<TaskPost> findByTaskIdOrderByCreatedAtAsc(@Param("taskId") Long taskId);
    
    // Count posts by task ID
    @Query("SELECT COUNT(tp) FROM TaskPost tp WHERE tp.task.id = :taskId")
    Long countByTaskId(@Param("taskId") Long taskId);
    
    // Find posts by author
    @Query("SELECT tp FROM TaskPost tp WHERE tp.author = :author ORDER BY tp.createdAt DESC")
    List<TaskPost> findByAuthorOrderByCreatedAtDesc(@Param("author") String author);
    
    // Find posts by task ID and author
    @Query("SELECT tp FROM TaskPost tp WHERE tp.task.id = :taskId AND tp.author = :author ORDER BY tp.createdAt DESC")
    List<TaskPost> findByTaskIdAndAuthorOrderByCreatedAtDesc(@Param("taskId") Long taskId, @Param("author") String author);
} 