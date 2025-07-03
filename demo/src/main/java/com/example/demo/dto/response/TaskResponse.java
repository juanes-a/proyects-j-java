package com.example.demo.dto.response;


import com.example.demo.entity.TaskEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String statusDisplayName;
    private String priority;
    private String priorityDisplayName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    
    private Integer estimatedHours;
    private Integer actualHours;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private Long projectId;
    private String projectName;
    
    private Long assignedUserId;
    private String assignedUserName;

    // Método para convertir de Entity a DTO
    public static TaskResponse fromEntity(TaskEntity task) {
        return TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .statusDisplayName(task.getStatus().getDisplayName())
                .priority(task.getPriority().name())
                .priorityDisplayName(task.getPriority().getDisplayName())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .estimatedHours(task.getEstimatedHours())
                .actualHours(task.getActualHours())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .projectId(task.ExposeProjectId())
                .projectName(task.getProjectName())
                .assignedUserId(task.getAssignedUser() != null ? task.getAssignedUser().getId() : null)
                .assignedUserName(task.getAssignedUser() != null ? 
                    task.getAssignedUser().getName() : null) 
                .build();
    }
}