package net.javaguides.springboot_backend.repositories;

import net.javaguides.springboot_backend.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    
    // Find all comments by post ID, ordered by creation date (oldest first)
    @Query("SELECT tc FROM TaskComment tc WHERE tc.post.id = :postId ORDER BY tc.createdAt ASC")
    List<TaskComment> findByPostIdOrderByCreatedAtAsc(@Param("postId") Long postId);
    
    // Find all comments by post ID, ordered by creation date (newest first)
    @Query("SELECT tc FROM TaskComment tc WHERE tc.post.id = :postId ORDER BY tc.createdAt DESC")
    List<TaskComment> findByPostIdOrderByCreatedAtDesc(@Param("postId") Long postId);
    
    // Count comments by post ID
    @Query("SELECT COUNT(tc) FROM TaskComment tc WHERE tc.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);
    
    // Find comments by author
    @Query("SELECT tc FROM TaskComment tc WHERE tc.author = :author ORDER BY tc.createdAt DESC")
    List<TaskComment> findByAuthorOrderByCreatedAtDesc(@Param("author") String author);
    
    // Find comments by post ID and author
    @Query("SELECT tc FROM TaskComment tc WHERE tc.post.id = :postId AND tc.author = :author ORDER BY tc.createdAt DESC")
    List<TaskComment> findByPostIdAndAuthorOrderByCreatedAtDesc(@Param("postId") Long postId, @Param("author") String author);
} 