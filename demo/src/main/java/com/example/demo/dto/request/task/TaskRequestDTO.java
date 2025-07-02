package com.example.demo.dto.request.task;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequestDTO {
    private String name;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer estimatedHours;
    private Integer actualHours;
    private Long projectId;
    private Long assignedUserId;
}
