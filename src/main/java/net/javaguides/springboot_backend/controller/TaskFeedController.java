package net.javaguides.springboot_backend.controller;

import net.javaguides.springboot_backend.entity.TaskComment;
import net.javaguides.springboot_backend.entity.TaskPost;
import net.javaguides.springboot_backend.payload.ApiResponse;
import net.javaguides.springboot_backend.service.TaskFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-feed")
public class TaskFeedController {
    
    @Autowired
    private TaskFeedService taskFeedService;
    
    // Get all posts for a task
    @GetMapping("/tasks/{taskId}/posts")
    public ResponseEntity<ApiResponse<List<TaskPost>>> getTaskPosts(@PathVariable Long taskId) {
        List<TaskPost> posts = taskFeedService.getTaskPosts(taskId);
        return ResponseEntity.ok(ApiResponse.success("Posts retrieved successfully", posts));
    }
    
    // Get a specific post by ID
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<TaskPost>> getPostById(@PathVariable Long postId) {
        TaskPost post = taskFeedService.getPostById(postId);
        return ResponseEntity.ok(ApiResponse.success("Post retrieved successfully", post));
    }
    
    // Create a new post for a task
    @PostMapping("/tasks/{taskId}/posts")
    public ResponseEntity<ApiResponse<TaskPost>> createPost(
            @PathVariable Long taskId, 
            @RequestBody TaskPost postRequest,
            @RequestParam String username) {
        TaskPost createdPost = taskFeedService.createPost(taskId, postRequest, username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Post created successfully", createdPost));
    }
    
    // Update a post
    @PutMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<TaskPost>> updatePost(
            @PathVariable Long postId, 
            @RequestBody TaskPost postRequest,
            @RequestParam String username) {
        TaskPost updatedPost = taskFeedService.updatePost(postId, postRequest, username);
        return ResponseEntity.ok(ApiResponse.success("Post updated successfully", updatedPost));
    }
    
    // Delete a post
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @PathVariable Long postId,
            @RequestParam String username) {
        taskFeedService.deletePost(postId, username);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully", null));
    }
    
    // Get all comments for a post
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<TaskComment>>> getPostComments(@PathVariable Long postId) {
        List<TaskComment> comments = taskFeedService.getPostComments(postId);
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }
    
    // Get a specific comment by ID
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<TaskComment>> getCommentById(@PathVariable Long commentId) {
        TaskComment comment = taskFeedService.getCommentById(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment retrieved successfully", comment));
    }
    
    // Create a new comment for a post
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<TaskComment>> createComment(
            @PathVariable Long postId, 
            @RequestBody TaskComment commentRequest,
            @RequestParam String username) {
        TaskComment createdComment = taskFeedService.createComment(postId, commentRequest, username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment created successfully", createdComment));
    }
    
    // Update a comment
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<TaskComment>> updateComment(
            @PathVariable Long commentId, 
            @RequestBody TaskComment commentRequest,
            @RequestParam String username) {
        TaskComment updatedComment = taskFeedService.updateComment(commentId, commentRequest, username);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", updatedComment));
    }
    
    // Delete a comment
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @PathVariable Long commentId,
            @RequestParam String username) {
        taskFeedService.deleteComment(commentId, username);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }
    
    // Get post count for a task
    @GetMapping("/tasks/{taskId}/post-count")
    public ResponseEntity<ApiResponse<Long>> getTaskPostCount(@PathVariable Long taskId) {
        Long postCount = taskFeedService.getTaskPostCount(taskId);
        return ResponseEntity.ok(ApiResponse.success("Post count retrieved successfully", postCount));
    }
    
    // Get comment count for a post
    @GetMapping("/posts/{postId}/comment-count")
    public ResponseEntity<ApiResponse<Long>> getPostCommentCount(@PathVariable Long postId) {
        Long commentCount = taskFeedService.getPostCommentCount(postId);
        return ResponseEntity.ok(ApiResponse.success("Comment count retrieved successfully", commentCount));
    }
    
    // Get posts by author for a specific task
    @GetMapping("/tasks/{taskId}/posts/author/{author}")
    public ResponseEntity<ApiResponse<List<TaskPost>>> getTaskPostsByAuthor(
            @PathVariable Long taskId, 
            @PathVariable String author) {
        List<TaskPost> posts = taskFeedService.getTaskPostsByAuthor(taskId, author);
        return ResponseEntity.ok(ApiResponse.success("Posts by author retrieved successfully", posts));
    }
    
    // Get all posts by author across all tasks
    @GetMapping("/posts/author/{author}")
    public ResponseEntity<ApiResponse<List<TaskPost>>> getAllPostsByAuthor(@PathVariable String author) {
        List<TaskPost> posts = taskFeedService.getAllPostsByAuthor(author);
        return ResponseEntity.ok(ApiResponse.success("All posts by author retrieved successfully", posts));
    }
} 