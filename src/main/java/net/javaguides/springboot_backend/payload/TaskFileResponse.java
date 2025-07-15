package net.javaguides.springboot_backend.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskFileResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String uploadedBy;
    private Long taskId;
} 