package net.javaguides.springboot_backend.service;

import net.javaguides.springboot_backend.entity.Task;
import net.javaguides.springboot_backend.entity.TaskComment;
import net.javaguides.springboot_backend.entity.TaskPost;
import net.javaguides.springboot_backend.exception.ResourceNotFoundException;
import net.javaguides.springboot_backend.repositories.TaskCommentRepository;
import net.javaguides.springboot_backend.repositories.TaskPostRepository;
import net.javaguides.springboot_backend.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TaskFeedService {
    
    @Autowired
    private TaskPostRepository taskPostRepository;
    
    @Autowired
    private TaskCommentRepository taskCommentRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private AuditService auditService;
    
    // Get all posts for a task
    public List<TaskPost> getTaskPosts(Long taskId) {
        // Verify task exists
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        
        return taskPostRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }
    
    // Get a specific post by ID
    public TaskPost getPostById(Long postId) {
        return taskPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }
    
    // Create a new post for a task
    public TaskPost createPost(Long taskId, TaskPost postRequest, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        
        TaskPost post = TaskPost.builder()
                .content(postRequest.getContent())
                .author(username)
                .task(task)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        TaskPost savedPost = taskPostRepository.save(post);
        
        // Audit the action
        auditService.createAuditTrail("Created post in task: " + task.getName() + " (Task ID: " + taskId + ", Post ID: " + savedPost.getId() + ")", username);
        
        return savedPost;
    }
    
    // Update a post
    public TaskPost updatePost(Long postId, TaskPost postRequest, String username) {
        TaskPost post = getPostById(postId);
        
        // Check if user is authorized to update this post
        if (!post.getAuthor().equals(username)) {
            throw new RuntimeException("You are not authorized to update this post");
        }
        
        post.setContent(postRequest.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        
        TaskPost updatedPost = taskPostRepository.save(post);
        
        // Audit the action
        auditService.createAuditTrail("Updated post in task: " + post.getTask().getName() + " (Task ID: " + post.getTask().getId() + ", Post ID: " + postId + ")", username);
        
        return updatedPost;
    }
    
    // Delete a post
    public void deletePost(Long postId, String username) {
        TaskPost post = getPostById(postId);
        
        // Check if user is authorized to delete this post
        if (!post.getAuthor().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this post");
        }
        
        // Audit the action before deletion
        auditService.createAuditTrail("Deleted post in task: " + post.getTask().getName() + " (Task ID: " + post.getTask().getId() + ", Post ID: " + postId + ")", username);
        
        taskPostRepository.deleteById(postId);
    }
    
    // Get all comments for a post
    public List<TaskComment> getPostComments(Long postId) {
        // Verify post exists
        if (!taskPostRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        
        return taskCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
    
    // Get a specific comment by ID
    public TaskComment getCommentById(Long commentId) {
        return taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
    }
    
    // Create a new comment for a post
    public TaskComment createComment(Long postId, TaskComment commentRequest, String username) {
        TaskPost post = getPostById(postId);
        
        TaskComment comment = TaskComment.builder()
                .content(commentRequest.getContent())
                .author(username)
                .post(post)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        TaskComment savedComment = taskCommentRepository.save(comment);
        
        // Audit the action
        auditService.createAuditTrail("Created comment on post in task: " + post.getTask().getName() + " (Task ID: " + post.getTask().getId() + ", Post ID: " + postId + ", Comment ID: " + savedComment.getId() + ")", username);
        
        return savedComment;
    }
    
    // Update a comment
    public TaskComment updateComment(Long commentId, TaskComment commentRequest, String username) {
        TaskComment comment = getCommentById(commentId);
        
        // Check if user is authorized to update this comment
        if (!comment.getAuthor().equals(username)) {
            throw new RuntimeException("You are not authorized to update this comment");
        }
        
        comment.setContent(commentRequest.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        
        TaskComment updatedComment = taskCommentRepository.save(comment);
        
        // Audit the action
        auditService.createAuditTrail("Updated comment on post in task: " + comment.getPost().getTask().getName() + " (Task ID: " + comment.getPost().getTask().getId() + ", Post ID: " + comment.getPost().getId() + ", Comment ID: " + commentId + ")", username);
        
        return updatedComment;
    }
    
    // Delete a comment
    public void deleteComment(Long commentId, String username) {
        TaskComment comment = getCommentById(commentId);
        
        // Check if user is authorized to delete this comment
        if (!comment.getAuthor().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }
        
        // Audit the action before deletion
        auditService.createAuditTrail("Deleted comment on post in task: " + comment.getPost().getTask().getName() + " (Task ID: " + comment.getPost().getTask().getId() + ", Post ID: " + comment.getPost().getId() + ", Comment ID: " + commentId + ")", username);
        
        taskCommentRepository.deleteById(commentId);
    }
    
    // Get post count for a task
    public Long getTaskPostCount(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        
        return taskPostRepository.countByTaskId(taskId);
    }
    
    // Get comment count for a post
    public Long getPostCommentCount(Long postId) {
        if (!taskPostRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        
        return taskCommentRepository.countByPostId(postId);
    }
    
    // Get posts by author for a specific task
    public List<TaskPost> getTaskPostsByAuthor(Long taskId, String author) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        
        return taskPostRepository.findByTaskIdAndAuthorOrderByCreatedAtDesc(taskId, author);
    }
    
    // Get all posts by author across all tasks
    public List<TaskPost> getAllPostsByAuthor(String author) {
        return taskPostRepository.findByAuthorOrderByCreatedAtDesc(author);
    }
} 